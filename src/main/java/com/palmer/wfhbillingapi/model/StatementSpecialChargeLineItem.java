package com.palmer.wfhbillingapi.model;

import java.math.BigDecimal;

public record StatementSpecialChargeLineItem(int specialChargeId, BigDecimal price, String description) {
}
