package com.palmer.wfhbillingapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
