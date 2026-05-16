package com.palmer.wfhbillingapi.model.catalog;

import java.math.BigDecimal;
import java.util.List;

public record PackageDetail(int id, int sortOrder, String name, BigDecimal defaultCost, List<Integer> serviceIds) {
}
