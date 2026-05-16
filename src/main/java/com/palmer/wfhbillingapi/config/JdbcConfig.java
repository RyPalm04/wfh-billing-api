package com.palmer.wfhbillingapi.config;

import com.palmer.wfhbillingapi.converter.PricingModeReadingConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.List;

/**
 * Registers custom Spring Data JDBC converters. Extends {@code AbstractJdbcConfiguration}
 * to wire in {@link com.palmer.wfhbillingapi.converter.PricingModeReadingConverter}, which
 * maps the lowercase {@code pricing_mode} strings stored in the database to the
 * {@code Merchandise.PricingMode} enum.
 */
@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {
    @Override
    protected List<?> userConverters() {
        return List.of(new PricingModeReadingConverter());
    }
}
