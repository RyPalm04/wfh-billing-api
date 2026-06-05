package com.palmer.wfhbillingapi.service;

import java.util.Optional;
import java.util.UUID;

public interface DesktopAuthService {
    String generateLicenseKey(UUID tenantId);
    Optional<String> getLicenseKey(UUID tenantId);
}
