package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.SpecialCharge;
import org.springframework.data.repository.ListCrudRepository;

public interface SpecialChargeRepository extends ListCrudRepository<SpecialCharge, Integer> {
}
