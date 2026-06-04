package com.palmer.wfhbillingapi.model.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Catalog entry for a funeral service (e.g. embalming, use of facilities, transfer of remains).
 * {@code includedInPackage} indicates whether this service is covered by the selected package price
 * and should therefore be excluded from the individual services subtotal.
 * Items are ordered by {@code sortOrder} to match the fixed positions on the billing statement.
 */
@Table("services")
public record Service(@Id int id, String name, int sortOrder, BigDecimal defaultCost, boolean requiresDescription,
                      boolean includedInPackage, UUID tenantId) {
}
