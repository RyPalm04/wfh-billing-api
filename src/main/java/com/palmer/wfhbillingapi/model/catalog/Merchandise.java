package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("merchandise")
public record Merchandise(@Id int id, int sortOrder, String name, BigDecimal defaultCost,
                          boolean requiresDescription, boolean salesTaxable, PricingMode pricingMode,
                          UUID tenantId) {

    public enum PricingMode {FLAT, PER_UNIT}
}