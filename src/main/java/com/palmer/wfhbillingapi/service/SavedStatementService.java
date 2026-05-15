package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.statement.StatementSummary;

import java.util.List;

public interface SavedStatementService {
    List<StatementSummary> findAll();

    SavedStatement findById(int id);

    SavedStatement insertSavedStatement(StatementRequest request);

    SavedStatement update(int id, StatementRequest request);

    int nextControlNumber();
}
