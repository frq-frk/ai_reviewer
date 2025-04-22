package com.saiyans.aicodereviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.saiyans.aicodereviewer.model.PullRequest;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    @Query("SELECT pr FROM PullRequest pr LEFT JOIN FETCH pr.files WHERE pr.reviewStatus = 'PENDING'")
    List<PullRequest> findPendingPullRequestsWithFiles();

}
