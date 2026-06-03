package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.settings.TenantSettings;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface TenantSettingsRepository extends ListCrudRepository<TenantSettings, Integer> {
    List<TenantSettings> findAllByTenantId(UUID tenantId);
}
