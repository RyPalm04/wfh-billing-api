package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.tenant.Tenant;
import com.palmer.wfhbillingapi.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
