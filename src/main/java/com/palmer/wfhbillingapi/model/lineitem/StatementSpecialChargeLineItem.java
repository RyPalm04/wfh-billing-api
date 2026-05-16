package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

/**
 * A special charge selected on a saved billing statement. {@code price} is the
 * effective price at the time the statement was saved. {@code description} holds
 * the user-entered detail for charges that require one (e.g. mileage notes).
 */
public record StatementSpecialChargeLineItem(int specialChargeId, BigDecimal price, String description) {
}
