package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.model.settings.TenantSettings;
import com.palmer.wfhbillingapi.repository.TenantSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private final TenantSettingsRepository settingsRepository;

    public SettingsController(TenantSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @GetMapping
    public TenantSettings getSettings() {
        LOGGER.debug("getSettings called");
        return settingsRepository.findById(1).orElseThrow();
    }

    @PutMapping
    public TenantSettings updateSettings(@RequestBody TenantSettings settings) {
        LOGGER.debug("updateSettings called, salesTaxRate={}", settings.salesTaxRate());
        return settingsRepository.save(new TenantSettings(1, settings.salesTaxRate(),  settings.tenantId()));
    }
}
