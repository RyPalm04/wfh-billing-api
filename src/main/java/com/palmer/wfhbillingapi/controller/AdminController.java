package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.tenant.Tenant;
import com.palmer.wfhbillingapi.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final TenantRepository tenantRepository;

    public AdminController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/tenants")
    public List<Tenant> getTenants() {
        logger.debug("getTenants called");
        return tenantRepository.findAll();
    }

    @GetMapping("/tenants/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String id) {
        logger.debug("getTenant called for id={}", id);
        return ResponseEntity.ok(tenantRepository.findById(UUID.fromString(id)).orElse(null));
    }

    @PostMapping("/tenants/{id}/suspend")
    public ResponseEntity<Void> suspendTenant(@PathVariable UUID id){
        logger.debug("suspendTenant called for tenant with id: {}", id);
        tenantRepository.updateStatusByTenantId(id, "suspended");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tenants/{id}/reactivate")
    public ResponseEntity<Void> reactivateTenant(@PathVariable UUID id){
        logger.debug("reactivateTenant called for tenant with id: {}", id);
        tenantRepository.updateStatusByTenantId(id, "active");
        return ResponseEntity.noContent().build();
    }
}
