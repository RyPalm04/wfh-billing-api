package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.model.statement.PdfResult;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.statement.StatementSummary;
import com.palmer.wfhbillingapi.service.PdfService;
import com.palmer.wfhbillingapi.service.SavedStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Exposes CRUD endpoints for saved billing statements and PDF generation.
 */
@RestController
@RequestMapping("/statements")
public class StatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementController.class);
    private final SavedStatementService savedStatementService;
    private final PdfService pdfService;

    public StatementController(SavedStatementService savedStatementService, PdfService pdfService) {
        this.savedStatementService = savedStatementService;
        this.pdfService = pdfService;
    }

    @GetMapping()
    public ResponseEntity<List<StatementSummary>> getStatements() {
        LOGGER.debug("getStatements called");
        return ResponseEntity.ok(savedStatementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedStatement> getStatement(@PathVariable int id) {
        LOGGER.debug("getStatement called with id {}", id);
        return ResponseEntity.ok(savedStatementService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<SavedStatement> addStatement(@RequestBody StatementRequest statementRequest) {
        LOGGER.debug("addStatement called");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStatementService.insertSavedStatement(statementRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedStatement> updateStatement(@PathVariable int id, @RequestBody StatementRequest statementRequest) {
        LOGGER.debug("updateStatement called with id {}", id);
        return ResponseEntity.ok(savedStatementService.update(id, statementRequest));
    }

    @GetMapping("/next-control-number")
    public int getNextControlNumber() {
        LOGGER.debug("getNextControlNumber called");
        return savedStatementService.nextControlNumber();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getStatementPdf(@PathVariable int id) throws IOException {
        LOGGER.debug("getStatementPdf called with id {}", id);
        PdfResult result = pdfService.generatePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-" + result.controlNumber() + ".pdf\"")
                .body(result.pdf());
    }
}
