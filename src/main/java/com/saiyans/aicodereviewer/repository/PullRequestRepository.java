package com.saiyans.aicodereviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiyans.aicodereviewer.model.PullRequest;
import com.saiyans.aicodereviewer.model.ReviewStatus;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

	List<PullRequest> findTop3ByReviewStatusOrderByCreatedAtAsc(ReviewStatus pending);
}
