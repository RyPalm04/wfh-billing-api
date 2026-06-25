package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.settings.TenantSettings;
import com.palmer.wfhbillingapi.repository.TenantSettingsRepository;
import com.palmer.wfhbillingapi.security.EternatelUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private final TenantSettingsRepository settingsRepository;

    public SettingsController(TenantSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @GetMapping
    public TenantSettings getSettings(Authentication authentication) {
        LOGGER.debug("getSettings called");
        EternatelUserPrincipal principal = (EternatelUserPrincipal) authentication.getPrincipal();
        return settingsRepository.findById(UUID.fromString(principal.tenantId())).orElseThrow();
    }

    @PutMapping
    public TenantSettings updateSettings(@RequestBody TenantSettings settings, Authentication authentication) {
        LOGGER.debug("updateSettings called, salesTaxRate={}", settings.salesTaxRate());
        EternatelUserPrincipal principal = (EternatelUserPrincipal) authentication.getPrincipal();
        return settingsRepository.save(new TenantSettings(UUID.fromString(principal.tenantId()), settings.salesTaxRate()));
    }
}
