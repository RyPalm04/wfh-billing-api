package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.catalog.CashAdvance;
import com.palmer.wfhbillingapi.model.catalog.CatalogBundle;
import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import com.palmer.wfhbillingapi.model.catalog.PackageDetail;
import com.palmer.wfhbillingapi.model.catalog.Service;
import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import com.palmer.wfhbillingapi.model.catalog.SpecialCharge;
import com.palmer.wfhbillingapi.repository.CashAdvanceRepository;
import com.palmer.wfhbillingapi.repository.MerchandiseRepository;
import com.palmer.wfhbillingapi.repository.ServicePackageRepository;
import com.palmer.wfhbillingapi.repository.ServiceRepository;
import com.palmer.wfhbillingapi.repository.SpecialChargeRepository;
import com.palmer.wfhbillingapi.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public CatalogController(CashAdvanceRepository cashAdvanceRepository, MerchandiseRepository merchandiseRepository,
                             ServiceRepository serviceRepository, ServicePackageRepository servicePackageRepository,
                             SpecialChargeRepository specialChargeRepository) {
        this.cashAdvanceRepository = cashAdvanceRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.serviceRepository = serviceRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.specialChargeRepository = specialChargeRepository;
    }

    @GetMapping("cash-advances")
    public List<CashAdvance> getCashAdvances() {
        LOGGER.debug("getCashAdvances called");
        return forTenant(cashAdvanceRepository::findAll, cashAdvanceRepository::findAllByTenantId);
    }

    @GetMapping("merchandise")
    public List<Merchandise> getMerchandise() {
        LOGGER.debug("getMerchandise called");
        return forTenant(merchandiseRepository::findAll, merchandiseRepository::findAllByTenantId);
    }

    @GetMapping("services")
    public List<Service> getServices() {
        LOGGER.debug("getServices called");
        return forTenant(serviceRepository::findAll, serviceRepository::findAllByTenantId);
    }

    @GetMapping("packages")
    public List<ServicePackage> getPackages(@RequestParam(defaultValue = "false") boolean includeLegacy) {
        LOGGER.debug("getPackages called, includeLegacy = {}", includeLegacy);
        return forTenant(servicePackageRepository::findAll, servicePackageRepository::findAllByTenantId)
                .stream()
                .filter(p -> includeLegacy || !p.isLegacyPackage())
                .toList();
    }

    @GetMapping("packages/{id}")
    public PackageDetail getPackageById(@PathVariable Integer id) {
        LOGGER.debug("getPackageById called");
        ServicePackage servicePackage = isPlatformAdmin() ?
                                        servicePackageRepository.findById(id).orElseThrow() :
                                        servicePackageRepository.findByIdAndTenantId(id, TenantContext.getTenantId()).orElseThrow();

        List<Integer> serviceIds = getServiceIds(id);

        return new PackageDetail(servicePackage.getId(), servicePackage.getSortOrder(), servicePackage.getName(),
                servicePackage.getDefaultCost(), serviceIds);
    }

    @GetMapping("special-charges")
    public List<SpecialCharge> getSpecialCharges() {
        LOGGER.debug("getSpecialCharges called");
        return forTenant(specialChargeRepository::findAll, specialChargeRepository::findAllByTenantId);
    }

    @GetMapping
    public CatalogBundle getCatalog() {
        LOGGER.debug("getCatalog called");
        List<PackageDetail> packages = getPackages(false).stream()
                                                         .map(p -> new PackageDetail(p.getId(), p.getSortOrder(),
                                                                 p.getName(), p.getDefaultCost(),
                                                                 getServiceIds(p.getId())))
                                                         .toList();

        return new CatalogBundle(packages,
                forTenant(serviceRepository::findAll, serviceRepository::findAllByTenantId),
                forTenant(merchandiseRepository::findAll, merchandiseRepository::findAllByTenantId),
                forTenant(specialChargeRepository::findAll, specialChargeRepository::findAllByTenantId),
                forTenant(cashAdvanceRepository::findAll, cashAdvanceRepository::findAllByTenantId));
    }

    private boolean isPlatformAdmin() {
        return "platform_admin".equals(TenantContext.getRole());
    }

    private List<Integer> getServiceIds(Integer id) {
        return isPlatformAdmin() ?
               servicePackageRepository.findServiceIdsByPackageId(id) :
               servicePackageRepository.findServiceIdsByPackageIdAndTenantId(id, TenantContext.getTenantId());
    }

    private <T> List<T> forTenant(Supplier<List<T>> allQuery, Function<UUID, List<T>> tenantQuery) {
        return isPlatformAdmin() ? allQuery.get() : tenantQuery.apply(TenantContext.getTenantId());
    }
}
