package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Catalog entry for a merchandise item (e.g. casket, urn, vault).
 * {@code pricingMode} controls whether the item is sold at a flat rate or per unit.
 * {@code salesTaxable} determines whether the item's price is included in the sales tax calculation.
 * Items are ordered by {@code sortOrder} to match the fixed positions on the billing statement.
 */
@Table("merchandise")
public class Merchandise {
    @Id
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean requiresDescription;
    private final boolean salesTaxable;
    private final PricingMode pricingMode;
    private final UUID tenantId;

    public Merchandise(int id, int sortOrder, String name, BigDecimal defaultCost,
                       boolean requiresDescription, boolean salesTaxable, PricingMode pricingMode, UUID tenantId) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.requiresDescription = requiresDescription;
        this.salesTaxable = salesTaxable;
        this.pricingMode = pricingMode;
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

    public boolean isSalesTaxable() {
        return salesTaxable;
    }

    public PricingMode getPricingMode() {
        return pricingMode;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public enum PricingMode {FLAT, PER_UNIT}
}
