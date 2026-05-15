package com.palmer.wfhbillingapi.model;

import java.math.BigDecimal;

public record StatementMerchandiseLineItem(int merchandiseId, BigDecimal price, String description) {
}
