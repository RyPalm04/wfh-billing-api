package com.palmer.wfhbillingapi.model.statement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lightweight projection of a saved statement used for list views. Contains only the
 * header fields needed to identify and sort statements — no line item data is included.
 */
public record StatementSummary(int id, int controlNumber, String servicesForName, LocalDate serviceDate,
                               LocalDateTime savedAt) {
}
