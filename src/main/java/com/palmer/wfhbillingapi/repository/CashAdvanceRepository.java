package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.CashAdvance;
import org.springframework.data.repository.ListCrudRepository;

public interface CashAdvanceRepository extends ListCrudRepository<CashAdvance, Integer> {
}
