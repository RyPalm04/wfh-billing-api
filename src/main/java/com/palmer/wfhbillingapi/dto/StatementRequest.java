package com.palmer.wfhbillingapi.dto;

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request body for creating or updating a billing statement. Mirrors {@code SavedStatement}
 * but omits {@code id} and {@code savedAt}, which are assigned by the server on write.
 */
public record StatementRequest(int controlNumber, String servicesForName, LocalDate dateOfDeath, String placeOfDeath,
                               LocalDate serviceDate, String reasonForEmbalming, Integer packageId, BigDecimal salesTaxRate,
                               BigDecimal payment, String packageName, BigDecimal packagePrice,
                               List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                               List<StatementSpecialChargeLineItem> specialCharges, List<StatementCashAdvanceLineItem> cashAdvances) {
}
