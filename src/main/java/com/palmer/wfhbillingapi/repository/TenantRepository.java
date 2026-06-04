package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.tenant.Tenant;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends ListCrudRepository<Tenant, UUID> {

    Optional<Tenant> findByStripeCustomerId(String stripeCustomerId);

    @Modifying
    @Query("Update tenants SET status = :status WHERE stripe_customer_id = :stripeCustomerId")
    void updateStatusByStripeCustomerId(String status, String stripeCustomerId);
}
