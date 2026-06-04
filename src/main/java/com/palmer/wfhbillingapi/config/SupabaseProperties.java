package com.palmer.wfhbillingapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public record SupabaseProperties(String jwksUri, String serviceRoleKey) {
}
