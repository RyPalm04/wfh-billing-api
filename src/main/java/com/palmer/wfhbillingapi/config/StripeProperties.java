package com.palmer.wfhbillingapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stripe")
public record StripeProperties(String secretKey, String webhookSecret, String priceId, String successUrl, String cancelUrl) {
}
