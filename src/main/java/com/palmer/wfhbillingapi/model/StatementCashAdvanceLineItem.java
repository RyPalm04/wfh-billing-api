package com.palmer.wfhbillingapi.model;

import java.math.BigDecimal;

public record StatementCashAdvanceLineItem(int cashAdvanceId, BigDecimal amount, String provider) {
}
