package com.palmer.wfhbillingapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("special_charges")
public class SpecialCharge {
    @Id
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean requiresDescription;

    public SpecialCharge(int id, int sortOrder, String name, BigDecimal defaultCost, boolean requiresDescription) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.requiresDescription = requiresDescription;
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
}
