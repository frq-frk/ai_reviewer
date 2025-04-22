package com.saiyans.aicodereviewer.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.saiyans.aicodereviewer.model.PullRequest;
import com.saiyans.aicodereviewer.model.PullRequestFile;
import com.saiyans.aicodereviewer.model.ReviewComment;
import com.saiyans.aicodereviewer.model.ReviewStatus;
import com.saiyans.aicodereviewer.repository.PullRequestRepository;
import com.saiyans.aicodereviewer.repository.ReviewCommentRepository;
import com.saiyans.aicodereviewer.service.LLMReviewService;

import jakarta.transaction.Transactional;

@Component
public class ReviewWorker {
	
	
	private static final Logger log = LoggerFactory.getLogger(ReviewWorker.class);


    private final PullRequestRepository prRepo;
    private final LLMReviewService llmService;
    private final ReviewCommentRepository reviewCommentRepo;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
//    @Value("${github.token}")
//    private String githubToken;

    public ReviewWorker(PullRequestRepository prRepo, LLMReviewService llmService, ReviewCommentRepository reviewCommentRepo) {
        this.prRepo = prRepo;
        this.llmService = llmService;
        this.reviewCommentRepo = reviewCommentRepo;
    }

    @Transactional
    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void processPendingReviews() {
        List<PullRequest> pendingPRs = prRepo.findPendingPullRequestsWithFiles();

        for (PullRequest pr : pendingPRs) {
            try {
                pr.setReviewStatus(ReviewStatus.PROCESSING);
                prRepo.save(pr);

                log.info("🤖 Running LLM review for PR #{}",  pr.getPrNumber());

                for (PullRequestFile file : pr.getFiles()) {
                    if (file.getPatch() == null) continue;
                    String review = llmService.getReviewForDiff(file.getFilename(), file.getPatch());

                    ReviewComment rc = new ReviewComment();
                    rc.setFilename(file.getFilename());
                    rc.setComment(review);
                    rc.setFile(file);

                    reviewCommentRepo.save(rc);
                    
                    String commentBody = generateCombinedComment(pr.getFiles());
                    postReviewCommentToGitHub(pr.getRepoOwner(), pr.getRepoName(), pr.getPrNumber(), commentBody, pr.getAccessToken());

                    log.info("📂 {}", file.getFilename());
                    log.info("💬 {}", review);
                }

                pr.setReviewStatus(ReviewStatus.DONE);
                prRepo.save(pr);

            } catch (Exception e) {
                e.printStackTrace();
                pr.setReviewStatus(ReviewStatus.FAILED);
                prRepo.save(pr);
            }
        }
    }
    
    private String generateCombinedComment(List<PullRequestFile> files) {
        StringBuilder sb = new StringBuilder();
        for (PullRequestFile file : files) {
            if (file.getComments() == null || file.getComments().isEmpty()) continue;

            sb.append("### 💡 Review for `").append(file.getFilename()).append("`\n");
            for (ReviewComment comment : file.getComments()) {
                sb.append("- ").append(comment.getComment()).append("\n");
            }
            sb.append("\n");
        }

        if (sb.length() == 0) {
            return "✅ No major suggestions. Great job!";
        }

        return sb.toString();
    }
    
    private void postReviewCommentToGitHub(String owner, String repo, Long prNumber, String commentBody, String token) {
        String url = String.format("https://api.github.com/repos/%s/%s/issues/%d/comments", owner, repo, prNumber);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // Injected from application.properties

        Map<String, String> body = new HashMap<>();
        body.put("body", commentBody);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
            log.info("✅ Posted review comment to GitHub PR #{}", prNumber);
        } catch (Exception e) {
        	log.error("❌ Failed to post comment to GitHub", e);
        }
    }


}
