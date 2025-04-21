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


//    @Value("${openai.api.key}")
//    private String apiKey;
    
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
                return choices.get(0).get("message").toString();
            })
            .block();
    }

    private String buildPrompt(String filename, String diff) {
        return String.format("""
                Review the following code diff from file: %s

                Only show comments about:
                - Code quality
                - Logic issues
                - Security or performance concerns
                - Best practices
                - Naming or formatting issues

                Provide bullet points. Be concise.

                ```diff
                %s
                ```
                """, filename, diff);
    }
}
