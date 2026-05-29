package com.palmer.wfhbillingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);
    private final RestClient restClient = RestClient.create();

    @Value("${github.token}")
    private String token;

    @Value("${github.repo}")
    private String repo;

    public void createIssue(String title, String body, List<String> labels) {
        log.debug("Creating GitHub issue: {}", title);
        ResponseEntity<Void> bodilessEntity = restClient.post()
                                                        .uri("https://api.github.com/repos/" + repo + "/issues")
                                                        .header("Authorization", "Bearer " + token)
                                                        .header("Accept", "application/vnd.github+json")
                                                        .header("X-GitHub-Api-Version", "2022-11-28")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(Map.of("title", title, "body", body, "labels", labels))
                                                        .retrieve()
                                                        .toBodilessEntity();

        if (bodilessEntity.getStatusCode().is2xxSuccessful()) {
            log.debug("Issue created: {}", title);
        }
    }
}