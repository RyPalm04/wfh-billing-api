package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.security.TenantContext;
import com.palmer.wfhbillingapi.service.DesktopAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RequestMapping("/desktop")
public class DesktopAuthController {
    Logger logger = LoggerFactory.getLogger(DesktopAuthController.class);

    private final DesktopAuthService desktopAuthService;

    public DesktopAuthController(DesktopAuthService desktopAuthService) {
        this.desktopAuthService = desktopAuthService;
    }

    @PostMapping("/license-key")
    public Map<String, String> generateLicenseKey() {
        UUID tenantId = TenantContext.getTenantId();
        logger.debug("getLicenseKey called for tenant {}", tenantId);
        String key = desktopAuthService.getLicenseKey(TenantContext.getTenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No license key found"));

        return Map.of("licenseKey", key);
    }
}
