package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.Service;
import org.springframework.data.repository.ListCrudRepository;

public interface ServiceRepository extends ListCrudRepository<Service, Integer> {
}
