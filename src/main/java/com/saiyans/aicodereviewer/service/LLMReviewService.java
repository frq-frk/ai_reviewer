package com.saiyans.aicodereviewer.service;

import java.time.Duration;
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
		this.webClient = WebClient.builder().baseUrl("https://api.openai.com/v1/chat/completions")
				.defaultHeader("Authorization", "Bearer " + apiKey).build();
	}

	public String getReviewForDiff(String filename, String diff) {

		String prompt = buildPrompt(filename, diff);

		log.info("prompt for llm: {}", prompt);

		Map<String, Object> request = Map.of("model", "gpt-3.5-turbo", "messages",
				List.of(Map.of("role", "system", "content", "You're a senior code reviewer."),
						Map.of("role", "user", "content", prompt)));

		return webClient.post().bodyValue(request).retrieve().bodyToMono(Map.class).retry(3)
				.timeout(Duration.ofSeconds(10)).map(resp -> {
					var choices = (List<Map<String, Object>>) resp.get("choices");
					var message = (Map<String, Object>) choices.get(0).get("message");
					return (String) message.get("content");
				}).block();
	}

	private String buildPrompt(String filename, String diff) {
		return String.format("""
				                You are an expert software engineer conducting a professional code review.

				Analyze the following code diff from the file: %s

				Only comment on:
				- Code quality and maintainability
				- Logical errors or edge cases
				- Performance or security concerns
				- Adherence to best practices
				- Naming conventions and formatting issues

				Strict instructions:
				- If the code is clean and you have no **actionable suggestions**, respond with an empty message.
				- Do not include compliments, praise, or general positive reinforcement.
				- Do not restate unchanged code or explain what the code is doing.
				- Do not mention you are an AI.

				Format:
				- Use clear, markdown-compatible bullet points
				- Avoid introductory or closing remarks


				                ```diff
				                %s
				                ```
				                """, filename, diff);
	}
}
