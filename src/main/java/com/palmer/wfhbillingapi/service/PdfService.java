package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.statement.PdfResult;

import java.io.IOException;

/**
 * Service for generating PDF billing statements.
 */
public interface PdfService {

    /**
     * Generates a PDF for the given statement by joining the saved line items against
     * the full catalog to build a positional JasperReports field map.
     *
     * @param statementId the database primary key of the statement to render
     * @return a {@link PdfResult} containing the raw PDF bytes and the control number
     * @throws IOException if the JasperReports template cannot be loaded or PDF generation fails
     */
    PdfResult generatePdf(int statementId) throws IOException;
}
