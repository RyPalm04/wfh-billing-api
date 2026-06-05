package com.palmer.wfhbillingapi.model.desktop;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("license_keys")
public record LicenseKey(@Id Integer id, UUID tenantId, UUID key, LocalDateTime createdAt, LocalDateTime revokedAt) {
}
