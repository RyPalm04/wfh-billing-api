package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("services")
public class Service {
    @Id
    private final int id;
    private final String name;
    private final int sortOrder;
    private final BigDecimal defaultCost;
    private final boolean includedInPackage;

    public Service(int id, String name, int sortOrder, BigDecimal defaultCost, boolean includedInPackage) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.defaultCost = defaultCost;
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

    public boolean isIncludedInPackage() {
        return includedInPackage;
    }
}
