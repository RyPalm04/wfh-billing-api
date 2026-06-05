package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.desktop.LicenseKey;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface LicenseKeyRepository extends ListCrudRepository<LicenseKey, Integer> {
    Optional<LicenseKey> findByKey(UUID key);
}
