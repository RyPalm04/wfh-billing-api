package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.desktop.LicenseKey;
import com.palmer.wfhbillingapi.repository.LicenseKeyRepository;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public class DesktopAuthServiceImpl implements DesktopAuthService {
    private static final Logger logger = LoggerFactory.getLogger(DesktopAuthServiceImpl.class);
    private static final int GROUP_SIZE = 5;
    private static final int GROUP_COUNT = 4;
    private static final String KEY_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final LicenseKeyRepository licenseKeyRepository;

    public DesktopAuthServiceImpl(LicenseKeyRepository licenseKeyRepository) {
        this.licenseKeyRepository = licenseKeyRepository;
    }

    @Override
    public String generateLicenseKey(UUID tenantId) {
        String key = buildKey();

        licenseKeyRepository.save(new LicenseKey(null, tenantId, key, LocalDateTime.now(), null));

        logger.info("Generated license key for tenant {}", tenantId);

        return key;
    }

    @Override
    public Optional<String> getLicenseKey(UUID tenantId) {
        return licenseKeyRepository.findByTenantId(tenantId).map(LicenseKey::key);
    }

    @Nonnull
    private static String buildKey() {
        SecureRandom random = new SecureRandom();
        StringJoiner stringJoiner = new StringJoiner("-", "EDC-", "");

        for (int g = 0; g < GROUP_SIZE; g++) {
            StringBuilder group = new StringBuilder(GROUP_SIZE);

            for (int c = 0; c < GROUP_COUNT; c++) {
                group.append(KEY_CHARS.charAt(random.nextInt(KEY_CHARS.length())));
            }

            stringJoiner.add(group);
        }

        return stringJoiner.toString();
    }
}
