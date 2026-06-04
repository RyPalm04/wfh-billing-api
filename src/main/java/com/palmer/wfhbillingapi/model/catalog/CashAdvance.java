package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("cash_advances")
public record CashAdvance(@Id int id, int sortOrder, String name, UUID tenantId) {
}
