package com.palmer.wfhbillingapi.dto;

public record FeedbackRequest(
        String type,
        String description,
        FeedbackMetadata metadata
) {
    public record FeedbackMetadata(
            String page,
            String userAgent,
            String screenSize,
            String referrer,
            String appVersion,
            String platform
    ) {
    }
}