package com.palmer.wfhbillingapi.model.settings;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("tenant_settings")
public record TenantSettings(@Id UUID tenantId, BigDecimal salesTaxRate) {
}
