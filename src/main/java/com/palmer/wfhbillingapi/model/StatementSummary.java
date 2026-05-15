package com.palmer.wfhbillingapi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StatementSummary(int id, int controlNumber, String servicesForName, LocalDate serviceDate,
                               LocalDateTime savedAt) {
}
