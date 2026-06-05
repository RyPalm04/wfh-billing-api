package com.palmer.wfhbillingapi.config;

import com.palmer.wfhbillingapi.repository.LicenseKeyRepository;
import com.palmer.wfhbillingapi.repository.TenantRepository;
import com.palmer.wfhbillingapi.security.JwtAuthFilter;
import com.palmer.wfhbillingapi.security.LicenseKeyAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(SupabaseProperties supabaseProperties, @Value("${api.key}") String apiKey,
                                       TenantRepository tenantRepository, LicenseKeyRepository licenseKeyRepository) {
        return new JwtAuthFilter(supabaseProperties.url() + "/auth/v1/.well-known/jwks.json", apiKey, tenantRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                           LicenseKeyAuthFilter licenseKeyAuthFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                               .requestMatchers("/version", "/webhook/stripe", "/desktop/activate").permitAll()
                                               .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) ->
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
            )
            .addFilterBefore(licenseKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, LicenseKeyAuthFilter.class);

        return http.build();
    }
}
