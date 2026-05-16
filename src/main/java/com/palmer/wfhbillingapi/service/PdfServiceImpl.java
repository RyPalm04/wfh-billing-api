package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.catalog.CashAdvance;
import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import com.palmer.wfhbillingapi.model.catalog.SpecialCharge;
import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;
import com.palmer.wfhbillingapi.model.statement.PdfResult;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.statement.StatementCalculator;
import com.palmer.wfhbillingapi.repository.CashAdvanceRepository;
import com.palmer.wfhbillingapi.repository.MerchandiseRepository;
import com.palmer.wfhbillingapi.repository.ServicePackageRepository;
import com.palmer.wfhbillingapi.repository.ServiceRepository;
import com.palmer.wfhbillingapi.repository.SpecialChargeRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * {@link PdfService} implementation using JasperReports to render billing statement PDFs.
 *
 * <p>The compiled {@code .jrxml} template is cached after the first call to avoid
 * recompiling on every request. The field map is built by joining the saved statement's
 * selected line items against the full ordered catalog — each catalog item maps to a
 * fixed positional field name in the template, with {@code null} written for unselected items.
 *
 * <p>Financial totals are delegated to {@link StatementCalculator}.
 */
@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private static final String JRXML_RESOURCE = "/pdf/pdfTemplate.jrxml";

    private static JasperReport compiledReport;

    private final SavedStatementService savedStatementService;
    private final ServiceRepository serviceRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final SpecialChargeRepository specialChargeRepository;
    private final CashAdvanceRepository  cashAdvanceRepository;
    private final ServicePackageRepository servicePackageRepository;

    public PdfServiceImpl(SavedStatementService savedStatementService, ServiceRepository serviceRepository,
                          MerchandiseRepository merchandiseRepository, SpecialChargeRepository specialChargeRepository,
                          CashAdvanceRepository cashAdvanceRepository, ServicePackageRepository servicePackageRepository) {
        this.savedStatementService = savedStatementService;
        this.serviceRepository = serviceRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.specialChargeRepository = specialChargeRepository;
        this.cashAdvanceRepository = cashAdvanceRepository;
        this.servicePackageRepository = servicePackageRepository;
    }

    @Override
    public PdfResult generatePdf(int statementId) throws IOException {
        SavedStatement stmt = savedStatementService.findById(statementId);

        List<com.palmer.wfhbillingapi.model.catalog.Service> services = StreamSupport.stream(serviceRepository.findAll().spliterator(), false).toList();
        List<Merchandise> merchandise = StreamSupport.stream(merchandiseRepository.findAll().spliterator(), false).toList();
        List<SpecialCharge> specialCharges = StreamSupport.stream(specialChargeRepository.findAll().spliterator(), false).toList();
        List<CashAdvance> cashAdvances = StreamSupport.stream(cashAdvanceRepository.findAll().spliterator(), false).toList();
        ServicePackage pkg = stmt.packageId() != null
                ? servicePackageRepository.findById(stmt.packageId()).orElse(null)
                : null;

        try {
            JasperPrint print = fill(stmt, services, merchandise, specialCharges, cashAdvances, pkg);
            return new PdfResult(JasperExportManager.exportReportToPdf(print), statementId);
        } catch (JRException e) {
            throw new IOException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private JasperPrint fill(SavedStatement statement, List<com.palmer.wfhbillingapi.model.catalog.Service> services, List<Merchandise> merchandise,
                             List<SpecialCharge> specialCharges, List<CashAdvance> cashAdvances, ServicePackage servicePackage) throws JRException, IOException {
        JasperReport report = compiledReport();
        Map<String, Object> row = buildFieldMap(statement, services, merchandise, specialCharges, cashAdvances, servicePackage);
        JRMapArrayDataSource ds = new JRMapArrayDataSource(new Map[]{row});
        return JasperFillManager.fillReport(report, new HashMap<>(), ds);
    }

    private static synchronized JasperReport compiledReport() throws JRException, IOException {
        if (compiledReport == null) {
            LOGGER.debug("Compiling Jasper report template");
            try (InputStream in = PdfServiceImpl.class.getResourceAsStream(JRXML_RESOURCE)) {
                if (in == null) {
                    throw new IOException("Missing template resource: " + JRXML_RESOURCE);
                }
                compiledReport = JasperCompileManager.compileReport(in);
            }
        }
        return compiledReport;
    }

    private Map<String, Object> buildFieldMap(SavedStatement stmt, List<com.palmer.wfhbillingapi.model.catalog.Service> services,
                                              List<Merchandise> merchandise, List<SpecialCharge> specialCharges,
                                              List<CashAdvance> cashAdvances, ServicePackage servicePackage) {
        Map<String, Object> m = new HashMap<>();

        m.put("controlNumber", stmt.controlNumber());
        m.put("servicesFor", Objects.requireNonNullElse(stmt.servicesForName(), ""));
        m.put("dateOfDeath", formatDate(stmt.dateOfDeath()));
        m.put("placeOfDeath", Objects.requireNonNullElse(stmt.placeOfDeath(), ""));
        m.put("serviceDate", formatDate(stmt.serviceDate()));
        m.put("embalmingReason", Objects.requireNonNullElse(stmt.reasonForEmbalming(), ""));
        m.put("packagePrice", servicePackage != null ? toDouble(servicePackage.getDefaultCost()) : null);

        String[] serviceFields = {
                "basicServicesPrice", "embalmingPrice", "otherPreparationPrice",
                "useForVisitationPrice", "useForFuneralPrice", "useForMemorialPrice",
                "useForGravesidePrice", "funeralCoachPrice", "pallbearerCarPrice",
                "serviceCarPrice", "transferOfRemainsPrice", "otherAPrice", "otherBPrice"
        };

        Map<Integer, StatementServiceLineItem> savedServices = stmt.services().stream()
                .collect(Collectors.toMap(StatementServiceLineItem::serviceId, s -> s));
        Map<Integer, StatementMerchandiseLineItem> savedMerch = stmt.merchandise().stream()
                .collect(Collectors.toMap(StatementMerchandiseLineItem::merchandiseId, merch -> merch));
        Map<Integer, StatementSpecialChargeLineItem> savedSpecial = stmt.specialCharges().stream()
                .collect(Collectors.toMap(StatementSpecialChargeLineItem::specialChargeId, s -> s));
        Map<Integer, StatementCashAdvanceLineItem> savedCash = stmt.cashAdvances().stream()
                .collect(Collectors.toMap(StatementCashAdvanceLineItem::cashAdvanceId, c -> c));

        for (int i = 0; i < Math.min(serviceFields.length, services.size()); i++) {
            com.palmer.wfhbillingapi.model.catalog.Service svc = services.get(i);
            m.put(serviceFields[i], savedServices.containsKey(svc.getId()) ? toDouble(svc.getDefaultCost()) : null);
        }

        String[] merchPriceFields = {
                "casketPrice", "urnPrice", "vaultPrice", "serviceAccessoryPrice",
                "registerBookPrice", "thankYouCardPrice", "memorialFolderPrice",
                "memorialVideoPrice", "jewelryPrice", "burialSupervisionPrice",
                "graveMarkerPrice", "otherMerchAPrice", "otherMerchBPrice"
        };
        String[] merchDescFields = {
                "casketDescription", "cremationDescription", "vaultDescription",
                null,
                "registerBookDescription", null, "folderDescription",
                "videoDescription", "jewelryDescription", null,
                null, "otherMerchADescription", "otherMerchBDescription"
        };

        for (int i = 0; i < Math.min(merchPriceFields.length, merchandise.size()); i++) {
            Merchandise merch = merchandise.get(i);
            StatementMerchandiseLineItem item = savedMerch.get(merch.getId());
            m.put(merchPriceFields[i], item != null ? toDouble(item.price()) : null);
            if (merchDescFields[i] != null) {
                m.put(merchDescFields[i], item != null ? Objects.requireNonNullElse(item.description(), "") : "");
            }
        }

        String[] specialChargePriceFields = {
                "graveSetupPrice", "cremationPrice", "mileagePrice",
                "remainsForwardingPrice", "remainsReceivingPrice", "vaultWeekendPrice",
                "immediateBurialPrice"
        };
        String[] specialChargeDescFields = {
                "graveDescription", null, "mileageDescription",
                null, null, null, null
        };

        for (int i = 0; i < Math.min(specialChargePriceFields.length, specialCharges.size()); i++) {
            SpecialCharge specialCharge = specialCharges.get(i);
            StatementSpecialChargeLineItem item = savedSpecial.get(specialCharge.getId());
            m.put(specialChargePriceFields[i], item != null ? toDouble(item.price()) : null);
            if (specialChargeDescFields[i] != null) {
                m.put(specialChargeDescFields[i], item != null ? Objects.requireNonNullElse(item.description(), "") : "");
            }
        }

        String[] cashAdvanceDetailFields = {
                "graveOpeningDetail", "weekendHolidayDetail", "newspaperADetail",
                "newspaperBDetail", "newspaperCDetail", "newspaperDDetail",
                "radioNoticeDetail", "ministerADetail", "ministerBDetail",
                "organistDetail", "singerADetail", "singerBDetail", "singerCDetail",
                "hairdresserDetail", "deathCertDetail", "outOfTownDetail",
                "markerDateDetail", "flowerDetail", "cashAdvOtherADetail", "cashAdvOtherBDetail"
        };
        String[] cashAdvancePriceFields = {
                "graveOpeningPrice", "weekendHolidayPrice", "newspaperAPrice",
                "newspaperBPrice", "newspaperCPrice", "newspaperDPrice",
                "radioNoticePrice", "ministerAPrice", "ministerBPrice",
                "organistPrice", "singerAPrice", "singerBPrice", "singerCPrice",
                "hairdresserPrice", "deathCertPrice", "outOfTownPrice",
                "markerDatePrice", "flowerPrice", "cashAdvOtherAPrice", "cashAdvOtherBPrice"
        };

        int cashCount = Math.min(cashAdvanceDetailFields.length, cashAdvances.size());

        for (int i = 0; i < cashCount; i++) {
            CashAdvance catalogItem = cashAdvances.get(i);
            StatementCashAdvanceLineItem item = savedCash.get(catalogItem.getId());
            m.put(cashAdvanceDetailFields[i], item != null ? Objects.requireNonNullElse(item.provider(), ""): "");
            m.put(cashAdvancePriceFields[i], item != null ? toDouble(item.amount()) : null);
        }

        Map<Integer, BigDecimal> servicePrices = services.stream()
                .collect(Collectors.toMap(com.palmer.wfhbillingapi.model.catalog.Service::getId,
                        com.palmer.wfhbillingapi.model.catalog.Service::getDefaultCost));
        Set<Integer> taxableMerchandiseIds = merchandise.stream()
                .filter(Merchandise::isSalesTaxable)
                .map(Merchandise::getId)
                .collect(Collectors.toSet());
        BigDecimal packageCost = servicePackage != null ?
                servicePackage.getDefaultCost() : null;

        m.put("totalServices", StatementCalculator.servicesTotal(stmt,
                servicePrices, packageCost).doubleValue());
        m.put("totalMerchandise",
                StatementCalculator.merchandiseTotal(stmt).doubleValue());
        m.put("totalSpecialCharges",
                StatementCalculator.specialChargesTotal(stmt).doubleValue());
        m.put("totalCashAdv",
                StatementCalculator.cashAdvancesTotal(stmt).doubleValue());
        m.put("salesTax", StatementCalculator.salesTax(stmt,
                taxableMerchandiseIds).doubleValue());
        m.put("salesTaxLabel", "Sales Tax " + stmt.salesTaxRate().multiply(new
                BigDecimal("100")).stripTrailingZeros().toPlainString() + "%");
        m.put("subTotal", StatementCalculator.subtotal(stmt, servicePrices,
                packageCost, taxableMerchandiseIds).doubleValue());
        m.put("downPayment", toDouble(stmt.payment()));
        m.put("finalTotal", StatementCalculator.finalTotal(stmt,
                servicePrices, packageCost, taxableMerchandiseIds).doubleValue());

        return m;
    }

    private static Double toDouble(BigDecimal bigDecimal) {
        return bigDecimal == null ? null : bigDecimal.doubleValue();
    }

    private static String formatDate(LocalDate d) {
        return d == null ? "" : d.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }
}
