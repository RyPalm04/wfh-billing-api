package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.CashAdvance;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface CashAdvanceRepository extends ListCrudRepository<CashAdvance, Integer> {
    List<CashAdvance> findAllByTenantId(UUID tenantId);

}
