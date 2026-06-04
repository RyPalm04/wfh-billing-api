package com.palmer.wfhbillingapi.service;

import java.util.UUID;

public interface SupabaseAdminService {
    void updateUserTenantMetadata(String supabaseUserId, UUID tenantId);
}
