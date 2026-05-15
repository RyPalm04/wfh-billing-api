package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import org.springframework.data.repository.CrudRepository;

public interface ServicePackageRepository extends CrudRepository<ServicePackage, Integer> {
}
