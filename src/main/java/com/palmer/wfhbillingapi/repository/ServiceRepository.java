package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<Service, Integer> {
}
