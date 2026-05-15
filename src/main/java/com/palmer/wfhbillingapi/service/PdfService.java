package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.statement.PdfResult;

import java.io.IOException;

public interface PdfService {

    PdfResult generatePdf(int statementId) throws IOException;
}
