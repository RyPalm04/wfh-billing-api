package com.palmer.wfhbillingapi.security;

import com.palmer.wfhbillingapi.repository.TenantRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final String apiKey;
    private final NimbusJwtDecoder jwtDecoder;
    private final TenantRepository tenantRepository;

    public JwtAuthFilter(String jwksUri, String apiKey, TenantRepository tenantRepository) {
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri)
                                          .jwsAlgorithm(SignatureAlgorithm.ES256)
                                          .build();
        this.apiKey = apiKey;
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if ("OPTIONS".equals(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    EternatelUserPrincipal principal = buildFromJwt(jwt);

                    if (!isSubscriptionActive(principal)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }

                    setSecurityContext(buildFromJwt(jwt));
                } catch (JwtException e) {
                    logger.error("Unauthorized access to jwt token", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            }

            String xApiKey = request.getHeader("X-Api-Key");

            if (xApiKey != null && xApiKey.equals(apiKey)) {
                setSecurityContext(new EternatelUserPrincipal("desktop", null, "DESKTOP"));
                filterChain.doFilter(request, response);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private EternatelUserPrincipal buildFromJwt(Jwt jwt) {
        String userId = jwt.getSubject();
        Map<String, Object> appMeta = jwt.getClaim("app_metadata");
        String tenantId = appMeta != null ? (String) appMeta.get("tenant_id") : null;
        String role = appMeta != null ? (String) appMeta.get("app_role") : null;

        return new EternatelUserPrincipal(userId, tenantId, role);
    }

    private boolean isSubscriptionActive(EternatelUserPrincipal principal) {
        if (principal.tenantId() == null) {
            return true;
        }

        return tenantRepository.findById(UUID.fromString(principal.tenantId()))
                .map(t -> "active".equals(t.status()) || "platform_manager".equals(t.status()))
                .orElse(false);
    }

    private void setSecurityContext(EternatelUserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return;
        }

        UsernamePasswordAuthenticationToken authorization = new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authorization);

        if (userPrincipal.tenantId() != null) {
            TenantContext.setTenantId(UUID.fromString(userPrincipal.tenantId()));
        }

        TenantContext.setRole(userPrincipal.role());
    }
}
