package com.palmer.wfhbillingapi.service;

import java.io.IOException;

public interface PdfService {

    byte[] generatePdf(int statementId) throws IOException;
}
