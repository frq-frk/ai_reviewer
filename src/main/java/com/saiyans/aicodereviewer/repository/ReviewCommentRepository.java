package com.saiyans.aicodereviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.saiyans.aicodereviewer.model.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
