package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServicePackageRepository extends ListCrudRepository<ServicePackage, Integer> {

    @Query("SELECT service_id FROM packaged_services WHERE package_id = :packageId ORDER BY service_id")
    List<Integer> findServiceIdsByPackageId(@Param("packageId") int packageId);
}
