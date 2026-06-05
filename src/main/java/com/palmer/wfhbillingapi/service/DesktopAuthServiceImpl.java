package com.palmer.wfhbillingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public class DesktopAuthServiceImpl implements DesktopAuthService {
    private static final Logger logger = LoggerFactory.getLogger(DesktopAuthServiceImpl.class);
    private static final int GROUP_SIZE = 5;
    private static final int GROUP_COUNT = 4;
    private static final String KEY_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Override
    public String generateActivationCode(UUID tenantId) {
        SecureRandom random = new SecureRandom();
        StringJoiner stringJoiner = new StringJoiner("-", "EDC-", "");

        for (int g = 0; g < GROUP_SIZE; g++) {
            StringBuilder group = new StringBuilder(GROUP_SIZE);

            for (int c = 0; c < GROUP_COUNT; c++) {
                group.append(KEY_CHARS.charAt(random.nextInt(KEY_CHARS.length())));
            }

            stringJoiner.add(group);
        }

        logger.info("Generated activation code for tenant {}", tenantId);

        return stringJoiner.toString();
    }
}
