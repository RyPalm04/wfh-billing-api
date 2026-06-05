package com.palmer.wfhbillingapi.service;

import java.util.UUID;

public interface DesktopAuthService {
    String generateActivationCode(UUID tenantId);
}
