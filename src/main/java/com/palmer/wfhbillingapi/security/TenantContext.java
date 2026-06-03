package com.palmer.wfhbillingapi.security;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    public static void setTenantId(UUID tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static UUID getTenantId() {
        return TENANT_ID.get();
    }

    public static void setRole(String role) {
        ROLE.set(role);
    }
    
    public static String getRole() {
        return ROLE.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        ROLE.remove();
    }
}
