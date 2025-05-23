package com.saiyans.aicodereviewer.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.saiyans.aicodereviewer.dto.ChangedFile;
import com.saiyans.aicodereviewer.dto.GitHubPullRequest;
import com.saiyans.aicodereviewer.github.GitHubJwtUtil;

@Service
public class GitHubApiService {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private final WebClient webClient;

    public GitHubApiService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.github.com").build();
    }

    public String getInstallationToken(long installationId) {
        String jwt = GitHubJwtUtil.generateJWT();

        Map<String, Object> response = webClient.post()
                .uri("/app/installations/{id}/access_tokens", installationId)
                .headers(headers -> {
                    headers.setBearerAuth(jwt);
                    headers.set("Accept", "application/vnd.github+json");
                })
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("token");
    }
    
    public GitHubPullRequest fetchPullRequest(String repoOwner, String repoName, int prNumber, String accessToken) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d", repoOwner, repoName, prNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GitHubPullRequest> response = restTemplate.exchange(url, HttpMethod.GET, request, GitHubPullRequest.class);
        return response.getBody();
    }

    public List<ChangedFile> fetchChangedFiles(String repoOwner, String repoName, int prNumber, String accessToken) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/files", repoOwner, repoName, prNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ChangedFile[]> response = restTemplate.exchange(url, HttpMethod.GET, request, ChangedFile[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

}
