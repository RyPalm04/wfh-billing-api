package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.desktop.ActivationCode;
import com.palmer.wfhbillingapi.model.desktop.LicenseKey;
import com.palmer.wfhbillingapi.repository.ActivationCodeRepository;
import com.palmer.wfhbillingapi.repository.LicenseKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DesktopAuthServiceImpl implements DesktopAuthService {
    private static final Logger logger = LoggerFactory.getLogger(DesktopAuthServiceImpl.class);
    private static final int CODE_LENGTH = 8;
    private static final int CODE_EXPIRY_MINUTES = 15;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final ActivationCodeRepository activationCodeRepository;
    private final LicenseKeyRepository licenseKeyRepository;

    public DesktopAuthServiceImpl(ActivationCodeRepository activationCodeRepository,
                                  LicenseKeyRepository licenseKeyRepository) {
        this.activationCodeRepository = activationCodeRepository;
        this.licenseKeyRepository = licenseKeyRepository;
    }

    @Override
    public String generateActivationCode(UUID tenantId) {
        String code = randomCode();

        activationCodeRepository.save(new ActivationCode(null, tenantId, code,
                LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES), null));

        logger.info("Generated activation code for tenant {}", tenantId);

        return code;
    }

    @Override
    @Transactional
    public UUID activate(String code) {
        ActivationCode activationCode = activationCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activation code"));

        if (activationCode.usedAt() != null) {
            throw new ResponseStatusException(HttpStatus.GONE, "Activation code is already used");
        }

        if (activationCode.expiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Activation code is expired");
        }

        activationCodeRepository.save(new ActivationCode(
                activationCode.id(), activationCode.tenantId(), activationCode.code(), activationCode.expiresAt(),
                LocalDateTime.now()));

        UUID key = UUID.randomUUID();
        licenseKeyRepository.save(new LicenseKey(null, activationCode.tenantId(), key, LocalDateTime.now(), null));

        logger.info("Activated license for tenant {}", activationCode.tenantId());
        return key;
    }

    private String randomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }

        return code.toString();
    }
}
