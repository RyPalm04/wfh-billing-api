package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

public record StatementSpecialChargeLineItem(int specialChargeId, BigDecimal price, String description) {
}
