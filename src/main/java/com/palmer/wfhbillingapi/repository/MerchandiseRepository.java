package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.Merchandise;
import org.springframework.data.repository.CrudRepository;

public interface MerchandiseRepository extends CrudRepository<Merchandise, Integer> {}
