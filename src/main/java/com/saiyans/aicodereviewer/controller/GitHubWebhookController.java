package com.saiyans.aicodereviewer.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiyans.aicodereviewer.service.WebhookService;

@RestController
@RequestMapping("/webhook")
public class GitHubWebhookController {

	private final WebhookService webhookService;

    public GitHubWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload,
                                                @RequestHeader("X-GitHub-Event") String eventType) {
        webhookService.processWebhook(payload, eventType);
        return ResponseEntity.ok("Received");
    }
}
