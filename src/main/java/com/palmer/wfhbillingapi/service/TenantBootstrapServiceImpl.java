package com.palmer.wfhbillingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class TenantBootstrapServiceImpl implements TenantBootstrapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantBootstrapServiceImpl.class);

    private static final String SEED_TENANT_SERVICES = """
            INSERT INTO services (sort_order, name, default_cost, included_in_package, requires_description, tenant_id)
            SELECT sort_order, name, default_cost, included_in_package, requires_description, :tenant_id
            FROM services
            WHERE tenant_id = '00000000-0000-0000-0000-000000000000'
            """;

    private static final String SEED_TENANT_MERCHANDISE = """
            INSERT INTO merchandise (sort_order, name, default_cost, requires_description, sales_taxable, pricing_mode, tenant_id)
            SELECT sort_order, name, default_cost, requires_description, sales_taxable, pricing_mode, :tenant_id
            FROM merchandise
            WHERE tenant_id = '00000000-0000-0000-0000-000000000000';
            """;

    private static final String SEED_TENANT_SPECIAL_CHARGES = """
            INSERT INTO special_charges (sort_order, name, default_cost, requires_description, tenant_id)
            SELECT sort_order, name, default_cost, requires_description, :tenant_id
            FROM special_charges
            WHERE tenant_id = '00000000-0000-0000-0000-000000000000';
            """;

    private static final String SEED_TENANT_CASH_ADVANCES = """
            INSERT INTO cash_advances (sort_order, name, tenant_id)
            SELECT sort_order, name, :tenant_id
            FROM cash_advances
            WHERE tenant_id = '00000000-0000-0000-0000-000000000000';
            """;

    private static final String SEED_TENANT_SERVICE_PACKAGES = """
            INSERT INTO service_packages (sort_order, name, default_cost, legacy_package, tenant_id)
            SELECT sort_order, name, default_cost, legacy_package, :tenant_id
            FROM service_packages
            WHERE tenant_id = '00000000-0000-0000-0000-000000000000';
            """;

    private static final String SEED_DEFAULT_SETTINGS = """
            INSERT INTO tenant_settings (tenant_id, sales_tax_rate) VALUES (:tenant_id, 0.0000)
            """;

    @Autowired
    private NamedParameterJdbcTemplate eternatelJdbcTemplate;

    @Override
    @Transactional
    public void seedTenantTables(UUID tenantId) {
        LOGGER.info("Seeding tenant tables for tenant: {}", tenantId);

        Map<String, UUID> params = Map.of("tenant_id", tenantId);

        eternatelJdbcTemplate.update(SEED_TENANT_SERVICES, params);
        eternatelJdbcTemplate.update(SEED_TENANT_MERCHANDISE, params);
        eternatelJdbcTemplate.update(SEED_TENANT_SPECIAL_CHARGES, params);
        eternatelJdbcTemplate.update(SEED_TENANT_CASH_ADVANCES, params);
        eternatelJdbcTemplate.update(SEED_TENANT_SERVICE_PACKAGES, params);
        eternatelJdbcTemplate.update(SEED_DEFAULT_SETTINGS, params);

        LOGGER.info("Successfully seeded tenant tables for tenant: {}", tenantId);
    }
}
