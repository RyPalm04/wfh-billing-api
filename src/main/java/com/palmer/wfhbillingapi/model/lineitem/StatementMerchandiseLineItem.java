package com.palmer.wfhbillingapi.model.lineitem;

import java.math.BigDecimal;

public record StatementMerchandiseLineItem(int merchandiseId, BigDecimal price, String description) {
}
