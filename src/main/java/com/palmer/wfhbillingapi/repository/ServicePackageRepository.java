package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicePackageRepository extends ListCrudRepository<ServicePackage, Integer> {

    List<ServicePackage> findAllByTenantId(UUID tenantId);

    @Query("SELECT service_id FROM packaged_services WHERE package_id = :packageId and tenant_id = :tenantId ORDER BY service_id")
    List<Integer> findServiceIdsByPackageIdAndTenantId(@Param("packageId") int packageId, @Param("tenantId") UUID tenantId);

    @Query("SELECT service_id FROM packaged_services WHERE package_id = :packageId ORDER BY service_id")
    List<Integer> findServiceIdsByPackageId(@Param("packageId") int packageId);

    Optional<ServicePackage> findByIdAndTenantId(int packageId, UUID tenantId);
}
