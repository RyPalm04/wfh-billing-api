package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Catalog entry for a funeral service (e.g. embalming, use of facilities, transfer of remains).
 * {@code includedInPackage} indicates whether this service is covered by the selected package price
 * and should therefore be excluded from the individual services subtotal.
 * Items are ordered by {@code sortOrder} to match the fixed positions on the billing statement.
 */
@Table("services")
public class Service {
    @Id
    private final int id;
    private final String name;
    private final int sortOrder;
    private final BigDecimal defaultCost;
    private final boolean requiresDescription;
    private final boolean includedInPackage;

    public Service(int id, String name, int sortOrder, BigDecimal defaultCost, boolean requiresDescription, boolean includedInPackage) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.defaultCost = defaultCost;
        this.requiresDescription = requiresDescription;
        this.includedInPackage = includedInPackage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public BigDecimal getDefaultCost() {
        return defaultCost;
    }

    public boolean isRequiresDescription() {
        return requiresDescription;
    }

    public boolean isIncludedInPackage() {
        return includedInPackage;
    }
}
