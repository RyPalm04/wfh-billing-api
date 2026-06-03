package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.Service;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends ListCrudRepository<Service, Integer> {
    List<Service> findAllByTenantId(UUID tenantId);
}
