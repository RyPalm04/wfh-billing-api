package com.palmer.wfhbillingapi.converter;

import com.palmer.wfhbillingapi.model.Merchandise;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PricingModeReadingConverter implements Converter<String, Merchandise.PricingMode> {
    @Override
    public Merchandise.PricingMode convert(String source) {
        return Merchandise.PricingMode.valueOf(source.toUpperCase());
    }
}
