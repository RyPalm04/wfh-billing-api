package com.palmer.wfhbillingapi.model.settings;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("tenant_settings")
public record TenantSettings(@Id int id, BigDecimal salesTaxRate) {
}
