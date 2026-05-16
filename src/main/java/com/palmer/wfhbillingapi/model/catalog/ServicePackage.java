package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Catalog entry for a pre-configured service package. A package bundles a set of services
 * at a fixed cost. When a package is selected on a statement, services marked
 * {@code includedInPackage} are excluded from the individual services subtotal.
 */
@Table("service_packages")
public class ServicePackage {
    @Id
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;

    public ServicePackage(int id, int sortOrder, String name, BigDecimal defaultCost) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
    }

    public int getId() {
        return id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getDefaultCost() {
        return defaultCost;
    }
}
