package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.settings.TenantSettings;
import org.springframework.data.repository.ListCrudRepository;

public interface TenantSettingsRepository extends ListCrudRepository<TenantSettings, Integer> {
}
