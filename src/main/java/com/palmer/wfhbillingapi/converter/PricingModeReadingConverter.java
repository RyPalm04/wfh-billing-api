package com.palmer.wfhbillingapi.converter;

import com.palmer.wfhbillingapi.model.catalog.Merchandise;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Converts the lowercase {@code pricing_mode} strings stored in the database
 * ({@code "flat"}, {@code "per_unit"}) to the {@code Merchandise.PricingMode} enum.
 * Required because the database stores the values in lowercase while the enum constants
 * are uppercase. Registered via {@link com.palmer.wfhbillingapi.config.JdbcConfig}.
 */
@ReadingConverter
public class PricingModeReadingConverter implements Converter<String, Merchandise.PricingMode> {
    @Override
    public Merchandise.PricingMode convert(String source) {
        return Merchandise.PricingMode.valueOf(source.toUpperCase());
    }
}
