package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

/**
 * A service selected on a saved billing statement. {@code inPackage} indicates
 * whether the service's cost is covered by the statement's selected package price,
 * in which case it is excluded from the individual services subtotal.
 */
public record StatementServiceLineItem(int serviceId, String name, BigDecimal price, boolean inPackage) {
}
