package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import org.springframework.data.repository.ListCrudRepository;

public interface MerchandiseRepository extends ListCrudRepository<Merchandise, Integer> {
}
