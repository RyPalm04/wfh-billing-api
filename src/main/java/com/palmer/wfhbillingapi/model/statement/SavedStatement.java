package com.palmer.wfhbillingapi.model.statement;

import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A fully hydrated billing statement as persisted in the database. Contains the statement
 * header fields plus all four line item collections (services, merchandise, special charges,
 * cash advances). Line item lists contain only selected items — unselected catalog entries
 * are not stored.
 */
public record SavedStatement(int id, int controlNumber, String servicesForName, LocalDate dateOfDeath,
                             String placeOfDeath, LocalDate serviceDate, String reasonForEmbalming, Integer packageId,
                             BigDecimal salesTaxRate, BigDecimal payment, LocalDateTime savedAt, ServicePackage servicePackage,
                             List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                             List<StatementSpecialChargeLineItem> specialCharges,
                             List<StatementCashAdvanceLineItem> cashAdvances) {
}
