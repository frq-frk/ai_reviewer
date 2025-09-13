package com.saiyans.aicodereviewer.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiyans.aicodereviewer.service.PushEventService;
import com.saiyans.aicodereviewer.service.WebhookService;

@RestController
@RequestMapping("/webhook")
public class GitHubWebhookController {

	private final PushEventService pushEventService;
	private final WebhookService webhookService;


    public GitHubWebhookController(PushEventService pushEventService,
			WebhookService webhookService) {
		super();
		this.pushEventService = pushEventService;
		this.webhookService = webhookService;
	}

	@PostMapping("/pr-review")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload,
                                                @RequestHeader("X-GitHub-Event") String eventType) {
        webhookService.processWebhook(payload, eventType);
        return ResponseEntity.ok("Received");
    }
    
    @PostMapping("/auto-fix")
    public ResponseEntity<String> handleGitHubEvent(
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload
    ) {
        System.out.println("📦 Received GitHub Event: " + eventType);
        pushEventService.processWebhook(payload, eventType);
        // TODO: Extract repo/branch/commit info and process further
        return ResponseEntity.ok("Event received");
    }
}
