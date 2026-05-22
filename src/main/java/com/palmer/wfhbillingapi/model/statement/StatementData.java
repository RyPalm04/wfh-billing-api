package com.palmer.wfhbillingapi.model.statement;

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;

import java.math.BigDecimal;
import java.util.List;

public record StatementData(List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                            List<StatementSpecialChargeLineItem> specialCharges, List<StatementCashAdvanceLineItem> cashAdvances,
                            String packageName, BigDecimal packagePrice) {
}
