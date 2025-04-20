package com.saiyans.aicodereviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiyans.aicodereviewer.model.PullRequestFile;

public interface PullRequestFileRepository extends JpaRepository<PullRequestFile, Long> {}