package com.palmer.wfhbillingapi.model.statement;

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public final class StatementCalculator {

    private StatementCalculator() {
    }

    public static BigDecimal servicesTotal(SavedStatement statement, Map<Integer, BigDecimal> servicePrices, BigDecimal packageCost) {
        BigDecimal pkg = packageCost != null ? packageCost : BigDecimal.ZERO;
        BigDecimal individual = safeAdd(statement.services().stream()
                .filter(s -> !s.inPackage())
                .map(s -> servicePrices.getOrDefault(s.serviceId(), BigDecimal.ZERO))
                .toArray(BigDecimal[]::new));
        return safeAdd(pkg, individual).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal merchandiseTotal(SavedStatement statement) {
        return safeAdd(statement.merchandise().stream()
                .map(StatementMerchandiseLineItem::price)
                .toArray(BigDecimal[]::new))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal specialChargesTotal(SavedStatement statement) {
        return safeAdd(statement.specialCharges().stream()
                .map(StatementSpecialChargeLineItem::price)
                .toArray(BigDecimal[]::new))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal cashAdvancesTotal(SavedStatement statement) {
        return safeAdd(statement.cashAdvances().stream()
                .map(StatementCashAdvanceLineItem::amount)
                .toArray(BigDecimal[]::new))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal salesTax(SavedStatement statement, Set<Integer> taxableMerchandiseIds) {
        BigDecimal taxableTotal = safeAdd(statement.merchandise().stream()
                .filter(merch -> taxableMerchandiseIds.contains(merch.merchandiseId()))
                .map(StatementMerchandiseLineItem::price)
                .toArray(BigDecimal[]::new));

        BigDecimal taxRate = statement.salesTaxRate() != null ? statement.salesTaxRate() : BigDecimal.ZERO;
        return taxableTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal subtotal(SavedStatement statement, Map<Integer, BigDecimal> servicePrices, BigDecimal packageCost, Set<Integer> taxableMerchandiseIds) {
        return safeAdd(servicesTotal(statement, servicePrices, packageCost), merchandiseTotal(statement), specialChargesTotal(statement), cashAdvancesTotal(statement), salesTax(statement, taxableMerchandiseIds));
    }

    public static BigDecimal finalTotal(SavedStatement statement, Map<Integer, BigDecimal> servicePrices, BigDecimal packageCost, Set<Integer> taxableMerchandiseIds) {
        BigDecimal sub = subtotal(statement, servicePrices, packageCost, taxableMerchandiseIds);
        BigDecimal payment = statement.payment() != null ? statement.payment() : BigDecimal.ZERO;
        return sub.subtract(payment).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal safeAdd(BigDecimal... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
