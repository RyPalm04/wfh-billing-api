package com.palmer.wfhbillingapi.service;

import java.util.UUID;

public interface TenantBootstrapService {
    void seedTenantTables(UUID tenantId);
}
