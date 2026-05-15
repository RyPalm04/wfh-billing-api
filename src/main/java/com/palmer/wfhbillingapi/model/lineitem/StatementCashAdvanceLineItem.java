package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

public record StatementCashAdvanceLineItem(int cashAdvanceId, BigDecimal amount, String provider) {
}
