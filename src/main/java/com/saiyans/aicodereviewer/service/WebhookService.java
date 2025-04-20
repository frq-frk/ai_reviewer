package com.saiyans.aicodereviewer.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.saiyans.aicodereviewer.dto.ChangedFile;
import com.saiyans.aicodereviewer.dto.GitHubPullRequest;
import com.saiyans.aicodereviewer.model.PullRequest;
import com.saiyans.aicodereviewer.model.PullRequestFile;
import com.saiyans.aicodereviewer.model.ReviewStatus;
import com.saiyans.aicodereviewer.repository.PullRequestRepository;

import jakarta.transaction.Transactional;

@Service
public class WebhookService {
	private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
	
	private final GitHubApiService githubApiService;
	private final PullRequestRepository pullRequestRepository;

    public WebhookService(GitHubApiService githubApiService, PullRequestRepository pullRequestRepository) {
        this.githubApiService = githubApiService;
        this.pullRequestRepository = pullRequestRepository;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void processWebhook(Map<String, Object> payload, String eventType) {
        if (!"pull_request".equals(eventType)) return;

        // Extract info
        Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
        Map<String, Object> repo = (Map<String, Object>) payload.get("repository");

        String repoOwner = ((Map<String, Object>) repo.get("owner")).get("login").toString();
        String repoName = repo.get("name").toString();
        int prNumber = ((Number) pullRequest.get("number")).intValue();

        GitHubPullRequest prData = githubApiService.fetchPullRequest(repoOwner, repoName, prNumber);
        List<ChangedFile> changedFiles = githubApiService.fetchChangedFiles(repoOwner, repoName, prNumber);

        PullRequest pr = new PullRequest();
        pr.setPrNumber((long) prData.getNumber());
        pr.setRepoOwner(repoOwner);
        pr.setRepoName(repoName);
        pr.setTitle(prData.getTitle());
        pr.setBody(prData.getBody());
        pr.setAuthor(prData.getUser().getLogin());
        pr.setReviewStatus(ReviewStatus.PENDING);

        for (ChangedFile file : changedFiles) {
            PullRequestFile fileEntity = new PullRequestFile();
            fileEntity.setFilename(file.getFilename());
            fileEntity.setPatch(file.getPatch());
            fileEntity.setPullRequest(pr);
            pr.getFiles().add(fileEntity);
        }

        pullRequestRepository.save(pr);
    }

}
