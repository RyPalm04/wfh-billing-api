package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.security.TenantContext;
import com.palmer.wfhbillingapi.service.DesktopAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;

@RequestMapping("/desktop")
public class DesktopAuthController {
    Logger logger = LoggerFactory.getLogger(DesktopAuthController.class);

    private final DesktopAuthService desktopAuthService;

    public DesktopAuthController(DesktopAuthService desktopAuthService) {
        this.desktopAuthService = desktopAuthService;
    }

    @PostMapping("/activation-code")
    public Map<String, String> generateCode() {
        logger.debug("Generating activation code for desktop");
        String code = desktopAuthService.generateActivationCode(TenantContext.getTenantId());

        return Map.of("code", code);
    }

    @PostMapping("/activate")
    public Map<String, String> activate(@RequestBody Map<String, String> payload) {
        logger.debug("Activating for desktop {}", payload);
        UUID key = desktopAuthService.activate(payload.get("code"));

        return Map.of("licenseKey", key.toString());
    }
}
