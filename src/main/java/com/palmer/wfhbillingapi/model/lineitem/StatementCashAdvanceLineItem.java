package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

/**
 * A cash advance selected on a saved billing statement. {@code amount} is the
 * actual expense paid on the family's behalf. {@code provider} identifies the
 * third party (e.g. the name of the minister or newspaper).
 */
public record StatementCashAdvanceLineItem(int cashAdvanceId, String name, BigDecimal amount, String provider) {
}
