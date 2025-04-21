package com.saiyans.aicodereviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saiyans.aicodereviewer.model.PullRequestFile;

@Repository
public interface PullRequestFileRepository extends JpaRepository<PullRequestFile, Long> {}