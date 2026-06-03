package com.palmer.wfhbillingapi.security;

import java.security.Principal;

public record EternatelUserPrincipal(String userId, String tenantId, String role) implements Principal {
    @Override
    public String getName() {
        return userId;
    }
}
