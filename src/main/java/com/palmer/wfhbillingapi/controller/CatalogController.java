package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.catalog.CashAdvance;
import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import com.palmer.wfhbillingapi.model.catalog.Service;
import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import com.palmer.wfhbillingapi.model.catalog.SpecialCharge;
import com.palmer.wfhbillingapi.repository.CashAdvanceRepository;
import com.palmer.wfhbillingapi.repository.MerchandiseRepository;
import com.palmer.wfhbillingapi.repository.ServicePackageRepository;
import com.palmer.wfhbillingapi.repository.ServiceRepository;
import com.palmer.wfhbillingapi.repository.SpecialChargeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes read-only catalog endpoints used to populate the billing statement UI.
 * All catalog data is seeded and managed separately; this API does not support writes.
 */
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogController.class);

    private final CashAdvanceRepository cashAdvanceRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final ServiceRepository serviceRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final SpecialChargeRepository specialChargeRepository;

    public CatalogController(CashAdvanceRepository cashAdvanceRepository, MerchandiseRepository merchandiseRepository, ServiceRepository serviceRepository, ServicePackageRepository servicePackageRepository, SpecialChargeRepository specialChargeRepository) {
        this.cashAdvanceRepository = cashAdvanceRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.serviceRepository = serviceRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.specialChargeRepository = specialChargeRepository;
    }

    @GetMapping("cash-advances")
    public Iterable<CashAdvance> getCashAdvances() {
        LOGGER.debug("getCashAdvances called");
        return cashAdvanceRepository.findAll();
    }

    @GetMapping("merchandise")
    public Iterable<Merchandise> getMerchandise() {
        LOGGER.debug("getMerchandise called");
        return merchandiseRepository.findAll();
    }

    @GetMapping("services")
    public Iterable<Service> getServices() {
        LOGGER.debug("getServices called");
        return serviceRepository.findAll();
    }

    @GetMapping("packages")
    public Iterable<ServicePackage> getPackages() {
        LOGGER.debug("getPackages called");
        return servicePackageRepository.findAll();
    }

    @GetMapping("special-charges")
    public Iterable<SpecialCharge> getSpecialCharges() {
        LOGGER.debug("getSpecialCharges called");
        return specialChargeRepository.findAll();
    }
}
