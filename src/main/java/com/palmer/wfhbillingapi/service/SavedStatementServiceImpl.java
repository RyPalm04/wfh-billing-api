package com.palmer.wfhbillingapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.mapper.SavedStatementRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementSummaryRowMapper;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.statement.StatementData;
import com.palmer.wfhbillingapi.model.statement.StatementSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

/**
 * {@link SavedStatementService} implementation backed by {@link JdbcTemplate}.
 * Multi-table reads (statement + four line item tables) are performed with separate
 * queries and assembled in {@link com.palmer.wfhbillingapi.mapper.SavedStatementRowMapper}.
 * Write operations are transactional and use a delete-and-reinsert strategy for line items.
 */
@Service
public class SavedStatementServiceImpl implements SavedStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SavedStatementServiceImpl.class);

    private static final String SELECT_ALL_SUMMARIES = """
            SELECT id, control_number, services_for_name, service_date, saved_at
            FROM saved_statements
            ORDER BY saved_at DESC
            """;

    private static final String SELECT_STATEMENT = """
            SELECT id, control_number, services_for_name, date_of_death, place_of_death,
                   service_date, reason_for_embalming, package_id, sales_tax_rate, payment, saved_at, data
            FROM saved_statements
            WHERE id = ?
            """;

    private static final String INSERT_STATEMENT = """
            INSERT INTO saved_statements
                (control_number, services_for_name, date_of_death, place_of_death,
                 service_date, reason_for_embalming, package_id, sales_tax_rate, payment, data)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)
            """;

    private static final String UPDATE_STATEMENT = """
            UPDATE saved_statements
            SET services_for_name = ?, date_of_death = ?, place_of_death = ?,
                service_date = ?, reason_for_embalming = ?, package_id = ?,
                sales_tax_rate = ?, payment = ?, data = ?::jsonb, saved_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

    private static final String SELECT_MAX_CONTROL_NUMBER = "SELECT MAX(control_number) FROM saved_statements";

    @Autowired
    private JdbcTemplate wfhBillingJdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<StatementSummary> findAll() {
        LOGGER.debug("Finding all saved statements");
        return wfhBillingJdbcTemplate.query(SELECT_ALL_SUMMARIES, new StatementSummaryRowMapper());
    }

    @Override
    public SavedStatement findById(int id) {
        LOGGER.debug("Finding saved statement by id {}", id);

        return wfhBillingJdbcTemplate.queryForObject(SELECT_STATEMENT, new SavedStatementRowMapper(objectMapper), id);
    }

    @Transactional
    @Override
    public SavedStatement insertSavedStatement(StatementRequest request) {
        LOGGER.debug("Inserting saved statement {}", request);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        wfhBillingJdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT, new String[]{"id"});

            preparedStatement.setInt(1, request.controlNumber());
            preparedStatement.setString(2, request.servicesForName());
            preparedStatement.setObject(3, request.dateOfDeath());
            preparedStatement.setString(4, request.placeOfDeath());
            preparedStatement.setObject(5, request.serviceDate());
            preparedStatement.setString(6, request.reasonForEmbalming());
            if (request.packageId() != null) {
                preparedStatement.setInt(7, request.packageId());
            } else {
                preparedStatement.setNull(7, Types.INTEGER);
            }
            preparedStatement.setBigDecimal(8, request.salesTaxRate());
            preparedStatement.setBigDecimal(9, request.payment());
            preparedStatement.setString(10, serializeData(request));
            return preparedStatement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (Objects.isNull(key)) {
            LOGGER.error("Insert saved statement key is null");
            throw new IllegalStateException("Insert succeeded but no generated key was returned");
        }

        int savedStatementId = key.intValue();

        return findById(savedStatementId);
    }

    @Transactional
    @Override
    public SavedStatement update(int id, StatementRequest request) {
        LOGGER.debug("Updating saved statement {}", request);
        Integer rowCount = wfhBillingJdbcTemplate.queryForObject("SELECT COUNT(*) FROM saved_statements WHERE id = ?", Integer.class, id);

        if (Objects.nonNull(rowCount) && !rowCount.equals(1)) {
            LOGGER.error("Statement requested to be updated not found. id={}", id);
            throw new IllegalStateException("Statement not found: " + id);
        }

        wfhBillingJdbcTemplate.update(UPDATE_STATEMENT,
                request.servicesForName(),
                request.dateOfDeath(),
                request.placeOfDeath(),
                request.serviceDate(),
                request.reasonForEmbalming(),
                request.packageId(),
                request.salesTaxRate(),
                request.payment(),
                serializeData(request),
                id
        );

        return findById(id);
    }

    private String serializeData(StatementRequest request) {
        try {
            StatementData data = new StatementData(
                    request.services(),
                    request.merchandise(),
                    request.specialCharges(),
                    request.cashAdvances()
            );

            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not serialize statement data", e);
            throw new IllegalStateException("Could not serialize statement data: ", e);
        }
    }

    @Override
    public int nextControlNumber() {
        LOGGER.debug("Next control number called");
        Integer max = wfhBillingJdbcTemplate.queryForObject(SELECT_MAX_CONTROL_NUMBER, Integer.class);

        if (max == null) {
            LOGGER.debug("Next control number is null returning 1");
            return 1;
        }

        LOGGER.debug("Next control number is {}", max + 1);
        return max + 1;
    }
}
