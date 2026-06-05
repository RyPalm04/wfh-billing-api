package com.palmer.wfhbillingapi.model.tenant;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("tenants")
public record Tenant(@Id UUID id, String name, String status, LocalDateTime createdAt, String supabaseUserId,
                     String stripeCustomerId, String stripeSubscriptionId) implements Persistable<UUID> {
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
