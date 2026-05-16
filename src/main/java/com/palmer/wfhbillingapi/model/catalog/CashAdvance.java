package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Catalog entry for a cash advance item. Cash advances are third-party expenses
 * paid on behalf of the family (e.g. grave opening, newspaper notices, ministers).
 * Items are ordered by {@code sortOrder} to match the fixed positions on the billing statement.
 */
@Table("cash_advances")
public class CashAdvance {
    @Id
    private final int id;
    private final int sortOrder;
    private final String name;

    public CashAdvance(int id, int sortOrder, String name) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
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
}
