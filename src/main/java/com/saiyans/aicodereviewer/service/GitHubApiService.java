package com.saiyans.aicodereviewer.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.saiyans.aicodereviewer.dto.ChangedFile;
import com.saiyans.aicodereviewer.dto.GitHubPullRequest;

@Service
public class GitHubApiService {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubPullRequest fetchPullRequest(String repoOwner, String repoName, int prNumber) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d", repoOwner, repoName, prNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GitHubPullRequest> response = restTemplate.exchange(url, HttpMethod.GET, request, GitHubPullRequest.class);
        return response.getBody();
    }

    public List<ChangedFile> fetchChangedFiles(String repoOwner, String repoName, int prNumber) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/files", repoOwner, repoName, prNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ChangedFile[]> response = restTemplate.exchange(url, HttpMethod.GET, request, ChangedFile[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }
}
