package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("service_packages")
public record ServicePackage(@Id int id, int sortOrder, String name, BigDecimal defaultCost,
                             boolean legacyPackage, UUID tenantId) {
}