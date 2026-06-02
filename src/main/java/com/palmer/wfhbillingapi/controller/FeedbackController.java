package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.dto.FeedbackRequest;
import com.palmer.wfhbillingapi.service.GitHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);
    private final GitHubService gitHubService;

    public FeedbackController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @PostMapping
    public ResponseEntity<Void> submitFeedback(@RequestBody FeedbackRequest request) {
        String truncated = request.description().length() > 60 ?
                           request.description().substring(0, 60) + "..." :
                           request.description();
        String title = "[Feedback] " + request.type() + ": " + truncated;

        String body = """
                ## Description
                %s
                
                ## Type
                %s
                
                ---
                ## Environment
                - **Page:** %s
                - **Browser:** %s
                - **Screen:** %s
                - **Referrer:** %s
                - **App Version:** %s
                - **Platform:** %s
                """.formatted(request.description(), request.type(), request.metadata().page(),
                              request.metadata().userAgent(), request.metadata().screenSize(),
                              request.metadata().referrer(), request.metadata().appVersion(),
                              request.metadata().platform());

        String label = switch (request.type()) {
            case "Bug" -> "bug";
            case "Feature Request" -> "feature-request";
            default -> "other";
        };

        try {
            gitHubService.createIssue(title, body, List.of("user-feedback", label));
        } catch (Exception e) {
            log.error("Failed to create GitHub issue for feedback", e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
