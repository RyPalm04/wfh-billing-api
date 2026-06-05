package com.palmer.wfhbillingapi.model.desktop;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("activation_codes")
public record ActivationCode(@Id Integer id, UUID tenantId, String code, LocalDateTime expiresAt, LocalDateTime usedAt) {
}
