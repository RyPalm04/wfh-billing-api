package com.palmer.wfhbillingapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SavedStatement(int id, int controlNumber, String servicesForName, LocalDate dateOfDeath,
                             String placeOfDeath, LocalDate serviceDate, String reasonForEmbalming, Integer packageId,
                             BigDecimal salesTaxRate, BigDecimal payment, LocalDateTime savedAt,
                             List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                             List<StatementSpecialChargeLineItem> specialCharges,
                             List<StatementCashAdvanceLineItem> cashAdvances) {
}
