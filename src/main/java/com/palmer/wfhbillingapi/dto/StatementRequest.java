package com.palmer.wfhbillingapi.dto;

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StatementRequest(int controlNumber, String servicesForName, LocalDate dateOfDeath, String placeOfDeath,
                               LocalDate serviceDate, String reasonForEmbalming, Integer packageId, BigDecimal salesTaxRate,
                               BigDecimal payment, List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                               List<StatementSpecialChargeLineItem> specialCharges, List<StatementCashAdvanceLineItem> cashAdvances) {
}
