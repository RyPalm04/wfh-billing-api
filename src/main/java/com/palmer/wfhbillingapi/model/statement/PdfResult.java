package com.palmer.wfhbillingapi.model.statement;

/**
 * Result of a PDF generation request. Carries the raw PDF bytes and the statement's
 * control number so the controller can set the correct {@code Content-Disposition} filename
 * without an additional database lookup.
 */
public record PdfResult(byte[] pdf, int controlNumber)  {}