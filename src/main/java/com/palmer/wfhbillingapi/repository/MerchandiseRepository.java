package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface MerchandiseRepository extends ListCrudRepository<Merchandise, Integer> {
    List<Merchandise> findAllByTenantId(UUID tenantId);

}
