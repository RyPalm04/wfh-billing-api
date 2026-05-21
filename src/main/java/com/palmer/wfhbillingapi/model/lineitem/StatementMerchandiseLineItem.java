package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

/**
 * A merchandise item selected on a saved billing statement. {@code price} is the
 * effective price at the time the statement was saved and may differ from the catalog
 * default. {@code description} holds the user-entered detail for items that require one.
 */
public record StatementMerchandiseLineItem(int merchandiseId, String name, BigDecimal price, String description) {
}
