package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.statement.StatementSummary;

import java.util.List;

/**
 * Service for CRUD operations on saved billing statements.
 */
public interface SavedStatementService {

    /**
     * Returns summary projections of all saved statements, ordered by most recently saved.
     */
    List<StatementSummary> findAll();

    /**
     * Returns the fully hydrated statement with all four line item collections.
     *
     * @param id the database primary key of the statement
     */
    SavedStatement findById(int id);

    /**
     * Persists a new statement and all its line items in a single transaction,
     * then returns the fully hydrated record including the generated ID.
     *
     * @param request the statement data to insert
     */
    SavedStatement insertSavedStatement(StatementRequest request);

    /**
     * Replaces the header and all line items of an existing statement in a single
     * transaction using a delete-and-reinsert strategy, then returns the updated record.
     *
     * @param id      the database primary key of the statement to update
     * @param request the replacement statement data
     */
    SavedStatement update(int id, StatementRequest request);

    /**
     * Returns the next available control number (MAX + 1), or 1 if no statements exist.
     */
    int nextControlNumber();

    /**
     * Deletes a statement by ID.
     *
     * @throws IllegalStateException if not found.
     *
     * @param id the database primary key of the statement to delete
     */
    void delete(int id);
}
