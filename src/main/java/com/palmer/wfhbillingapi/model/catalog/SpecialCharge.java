package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("special_charges")
public record SpecialCharge(@Id int id, int sortOrder, String name, BigDecimal defaultCost,
                            boolean requiresDescription, UUID tenantId) {
}