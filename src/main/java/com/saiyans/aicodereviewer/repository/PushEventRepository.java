package com.saiyans.aicodereviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiyans.aicodereviewer.model.PushEvent;
import com.saiyans.aicodereviewer.model.ReviewStatus;

public interface PushEventRepository extends JpaRepository<PushEvent, Long> {

	List<PushEvent> findByReviewStatus(ReviewStatus pending);
}
