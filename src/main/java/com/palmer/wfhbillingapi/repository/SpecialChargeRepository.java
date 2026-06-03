package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.SpecialCharge;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface SpecialChargeRepository extends ListCrudRepository<SpecialCharge, Integer> {
    List<SpecialCharge> findAllByTenantId(UUID tenantId);

}
