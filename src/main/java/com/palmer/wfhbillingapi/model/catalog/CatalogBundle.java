package com.palmer.wfhbillingapi.model.catalog;

import java.util.List;

public record CatalogBundle(List<PackageDetail> packages, List<Service> services, List<Merchandise> merchandise,
                            List<SpecialCharge> specialCharges, List<CashAdvance> cashAdvances) {
}
