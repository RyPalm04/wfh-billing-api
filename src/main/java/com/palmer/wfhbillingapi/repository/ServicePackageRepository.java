package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.ServicePackage;
import org.springframework.data.repository.CrudRepository;

public interface ServicePackageRepository extends CrudRepository<ServicePackage, Integer> {
}
