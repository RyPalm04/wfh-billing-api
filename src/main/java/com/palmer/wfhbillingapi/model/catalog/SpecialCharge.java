package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Catalog entry for a special charge (e.g. grave setup, cremation, mileage).
 * Special charges are distinct from cash advances in that they are direct charges
 * from the funeral home rather than third-party expenses.
 * Items are ordered by {@code sortOrder} to match the fixed positions on the billing statement.
 */
@Table("special_charges")
public class SpecialCharge {
    @Id
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean requiresDescription;
    private final UUID tenantId;

    public SpecialCharge(int id, int sortOrder, String name, BigDecimal defaultCost, boolean requiresDescription,
                         UUID tenantId) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.requiresDescription = requiresDescription;
        this.tenantId = tenantId;
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

    public boolean isRequiresDescription() {
        return requiresDescription;
    }

    public UUID getTenantId() {
        return tenantId;
    }
}
