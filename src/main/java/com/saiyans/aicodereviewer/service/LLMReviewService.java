package com.saiyans.aicodereviewer.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LLMReviewService {
	
	
	private static final Logger log = LoggerFactory.getLogger(LLMReviewService.class);
    
    private final WebClient webClient;

    public LLMReviewService(@Value("${openai.api.key}") String apiKey) {
    	log.info(apiKey);
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String getReviewForDiff(String filename, String diff) {
    	
    	String prompt = buildPrompt(filename, diff);

        log.info("prompt for llm: {}", prompt);
        
        Map<String, Object> request = Map.of(
            "model", "gpt-3.5-turbo",
            "messages", List.of(
                Map.of("role", "system", "content", "You're a senior code reviewer."),
                Map.of("role", "user", "content", prompt)
            )
        );

        return webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class)
            .map(resp -> {
                var choices = (List<Map<String, Object>>) resp.get("choices");
                var message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            })
            .block();
    }

    private String buildPrompt(String filename, String diff) {
        return String.format("""
                You are an expert software engineer conducting a professional code review.

                Analyze the following code diff from the file: %s

                Focus only on:
                - Code quality and maintainability
                - Logical errors or edge cases
                - Performance or security concerns
                - Adherence to best practices
                - Naming conventions and formatting issues

                Respond with concise, constructive bullet points. Do not explain the diff or restate unchanged code.

                Format:
                - Use clear, markdown-compatible bullet points
                - Do not include introductory or closing remarks
                - Avoid stating you're an AI

                ```diff
                %s
                ```
                """, filename, diff);
    }
}
