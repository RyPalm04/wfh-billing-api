package com.palmer.wfhbillingapi.security;

import com.palmer.wfhbillingapi.model.desktop.LicenseKey;
import com.palmer.wfhbillingapi.repository.LicenseKeyRepository;
import com.palmer.wfhbillingapi.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class LicenseKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseKeyAuthFilter.class);

    private final LicenseKeyRepository licenseKeyRepository;
    private final TenantRepository tenantRepository;

    public LicenseKeyAuthFilter(LicenseKeyRepository licenseKeyRepository,
                                TenantRepository tenantRepository) {
        this.licenseKeyRepository = licenseKeyRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("X-License-Key");

        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID keyValue;
        try {
            keyValue = UUID.fromString(header);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        LicenseKey licenseKey = licenseKeyRepository.findByKey(keyValue).orElse(null);
        if (licenseKey == null || licenseKey.revokedAt() != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        boolean active = tenantRepository.findById(licenseKey.tenantId())
                                         .map(t -> "active".equals(t.status()) || "platform_manager".equals(t.status()))
                                         .orElse(false);
        if (!active) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        EternatelUserPrincipal principal = new EternatelUserPrincipal("desktop", licenseKey.tenantId().toString(), "DESKTOP", null);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        TenantContext.setTenantId(licenseKey.tenantId());
        TenantContext.setRole("DESKTOP");

        filterChain.doFilter(request, response);
    }
}