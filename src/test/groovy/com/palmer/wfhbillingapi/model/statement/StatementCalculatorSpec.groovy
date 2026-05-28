package com.palmer.wfhbillingapi.model.statement

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class StatementCalculatorSpec extends Specification {

    private static SavedStatement statement(List services = [], List merchandise = [], List specialCharges = [], List cashAdvances = [], BigDecimal taxRate = 0.0825G, BigDecimal payment = null) {
        new SavedStatement(1, 1, "Test", null, "Memphis, TN", LocalDate.now(), "", 1, taxRate, payment, LocalDateTime.now(), null, services, merchandise, specialCharges, cashAdvances)
    }

    // --- servicesTotal ---

    def "servicesTotal with no services and no package returns zero"() {
        expect:
        StatementCalculator.servicesTotal(statement(), [:], null) == 0.00G
    }

    def "servicesTotal sums individual services not in a package"() {
        given:
        def services = [
                new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, false),
                new StatementServiceLineItem(2, "Embalming", 795.00G, null, false)
        ]

        expect:
        StatementCalculator.servicesTotal(statement(services), [:], null) == 2690.00G
    }

    def "servicesTotal uses catalog price over line item price when available"() {
        given:
        def services = [new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, false)]
        def catalogPrices = [1: 2000.00G]

        expect:
        StatementCalculator.servicesTotal(statement(services), catalogPrices, null) == 2000.00G
    }

    def "servicesTotal excludes services that are in a package"() {
        given:
        def services = [
                new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, true),
                new StatementServiceLineItem(2, "Embalming", 795.00G, null, false)
        ]

        expect:
        StatementCalculator.servicesTotal(statement(services), [:], null) == 795.00G
    }

    def "servicesTotal adds package cost when provided"() {
        given:
        def services = [new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, true)]

        expect:
        StatementCalculator.servicesTotal(statement(services), [:], 5995.00G) == 5995.00G
    }

    // --- salesTax ---

    def "salesTax returns zero when tax rate is null"() {
        given:
        def merch = [new StatementMerchandiseLineItem(1, "Casket", 2500.00G, "")]
        def taxableIds = [1] as Set

        expect:
        StatementCalculator.salesTax(statement([], merch, [], [], null), taxableIds) == 0.00G
    }

    def "salesTax returns zero when no taxable merchandise is selected"() {
        given:
        def merch = [new StatementMerchandiseLineItem(2, "Urn", 300.00G, "")]
        def taxableIds = [1] as Set

        expect:
        StatementCalculator.salesTax(statement([], merch), taxableIds) == 0.00G
    }

    def "salesTax calculates correctly for taxable merchandise"() {
        given:
        def merch = [new StatementMerchandiseLineItem(1, "Casket", 2500.00G, "")]
        def taxableIds = [1] as Set

        expect:
        StatementCalculator.salesTax(statement([], merch), taxableIds) == 206.25G
    }

    def "salesTax only taxes merchandise in the taxable set"() {
        given:
        def merch = [
                new StatementMerchandiseLineItem(1, "Casket", 2500.00G, ""),
                new StatementMerchandiseLineItem(2, "Urn", 300.00G, "")
        ]
        def taxableIds = [1] as Set

        expect:
        StatementCalculator.salesTax(statement([], merch), taxableIds) == 206.25G
    }

    // --- merchandiseTotal ---

    def "merchandiseTotal returns zero with no merchandise"() {
        expect:
        StatementCalculator.merchandiseTotal(statement()) == 0.00G
    }

    def "merchandiseTotal sums all merchandise prices"() {
        given:
        def merch = [
                new StatementMerchandiseLineItem(1, "Casket", 2500.00G, ""),
                new StatementMerchandiseLineItem(2, "Urn", 300.00G, "")
        ]

        expect:
        StatementCalculator.merchandiseTotal(statement([], merch)) == 2800.00G
    }

    // --- specialChargesTotal / cashAdvancesTotal ---

    def "specialChargesTotal sums all special charges"() {
        given:
        def charges = [
                new StatementSpecialChargeLineItem(1, "Grave Setup", 350.00G, null),
                new StatementSpecialChargeLineItem(2, "Death Certificates", 50.00G, null)
        ]

        expect:
        StatementCalculator.specialChargesTotal(statement([], [], charges)) == 400.00G
    }

    def "cashAdvancesTotal sums all cash advances"() {
        given:
        def advances = [
                new StatementCashAdvanceLineItem(1, "Grave Opening", 800.00G, null),
                new StatementCashAdvanceLineItem(2, "Obituary", 150.00G, null)
        ]

        expect:
        StatementCalculator.cashAdvancesTotal(statement([], [], [], advances)) == 950.00G
    }

    // --- finalTotal ---

    def "finalTotal subtracts payment from subtotal"() {
        given:
        def services = [new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, false)]

        expect:
        StatementCalculator.finalTotal(statement(services, [], [], [], 0.00G, 500.00G), [:], null, [] as Set) == 1395.00G
    }

    def "finalTotal treats null payment as zero"() {
        given:
        def services = [new StatementServiceLineItem(1, "Basic Services", 1895.00G, null, false)]

        expect:
        StatementCalculator.finalTotal(statement(services, [], [], [], 0.00G, null), [:], null, [] as Set) == 1895.00G
    }
}