package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.mapper.SavedStatementRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementCashAdvanceLineItemRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementMerchandiseLineItemRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementServiceLineItemRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementSpecialChargeLineItemRowMapper;
import com.palmer.wfhbillingapi.mapper.StatementSummaryRowMapper;
import com.palmer.wfhbillingapi.model.SavedStatement;
import com.palmer.wfhbillingapi.model.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.StatementSpecialChargeLineItem;
import com.palmer.wfhbillingapi.model.StatementSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Service
public class SavedStatementServiceImpl implements SavedStatementService {

    private static final String SELECT_ALL_SUMMARIES = "SELECT id, control_number, services_for_name, service_date, saved_at " + "FROM saved_statements ORDER BY saved_at DESC";

    private static final String SELECT_STATEMENT = "SELECT id, control_number, services_for_name, date_of_death, place_of_death, " + "service_date, reason_for_embalming, package_id, sales_tax_rate, payment " + "FROM saved_statements WHERE id = ?";

    private static final String SELECT_SERVICES = "SELECT service_id, in_package FROM saved_statement_services WHERE statement_id = ?";

    private static final String SELECT_MERCHANDISE = "SELECT merchandise_id, price, description FROM saved_statement_merchandise WHERE statement_id = ?";

    private static final String SELECT_SPECIAL_CHARGES = "SELECT special_charge_id, price, description FROM saved_statement_special_charges WHERE statement_id = ?";

    private static final String SELECT_CASH_ADVANCES = "SELECT cash_advance_id, amount, provider FROM saved_statement_cash_advances WHERE statement_id = ?";

    private static final String INSERT_STATEMENT = "INSERT INTO saved_statements (control_number, services_for_name, date_of_death, place_of_death, service_date, reason_for_embalming, package_id, sales_tax_rate, payment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_SERVICE = "INSERT INTO saved_statement_services (statement_id, service_id, in_package) VALUES (?, ?, ?)";

    private static final String INSERT_MERCHANDISE = "INSERT INTO saved_statement_merchandise (statement_id, merchandise_id, price, description) " + "VALUES (?, ?, ?, ?)";

    private static final String INSERT_SPECIAL_CHARGE = "INSERT INTO saved_statement_special_charges (statement_id, special_charge_id, price, description) " + "VALUES (?, ?, ?, ?)";

    private static final String INSERT_CASH_ADVANCE = "INSERT INTO saved_statement_cash_advances (statement_id, cash_advance_id, amount, provider) " + "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE saved_statements SET services_for_name = ?, date_of_death = ?, place_of_death = ?, " + "service_date = ?, reason_for_embalming = ?, package_id = ?, sales_tax_rate = ?, payment = ?, saved_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String DELETE_SERVICE = "DELETE FROM saved_statement_services WHERE statement_id = ?;";

    private static final String DELETE_MERCHANDISE = "DELETE FROM saved_statement_merchandise WHERE statement_id = ?;";

    private static final String DELETE_SPECIAL_CHARGE = "DELETE FROM saved_statement_special_charges WHERE statement_id = ?;";

    private static final String DELETE_CASH_ADVANCES = "DELETE FROM saved_statement_cash_advances WHERE statement_id = ?";

    private static final String SELECT_MAX_CONTROL_NUMBER = "SELECT MAX(control_number) FROM saved_statements";

    @Autowired
    JdbcTemplate wfhBillingJdbcTemplate;

    /**
     * @return
     */
    @Override
    public List<StatementSummary> findAll() {
        return wfhBillingJdbcTemplate.query(SELECT_ALL_SUMMARIES, new StatementSummaryRowMapper());
    }

    /**
     * @param id
     *
     * @return
     */
    @Override
    public SavedStatement findById(int id) {
        List<StatementServiceLineItem> statementServiceLineItems = wfhBillingJdbcTemplate.query(SELECT_SERVICES, new StatementServiceLineItemRowMapper(), id);
        List<StatementMerchandiseLineItem> statementMerchandiseLineItems = wfhBillingJdbcTemplate.query(SELECT_MERCHANDISE, new StatementMerchandiseLineItemRowMapper(), id);
        List<StatementSpecialChargeLineItem> statementSpecialChargeLineItems = wfhBillingJdbcTemplate.query(SELECT_SPECIAL_CHARGES, new StatementSpecialChargeLineItemRowMapper(), id);
        List<StatementCashAdvanceLineItem> statementCashAdvanceLineItems = wfhBillingJdbcTemplate.query(SELECT_CASH_ADVANCES, new StatementCashAdvanceLineItemRowMapper(), id);

        return wfhBillingJdbcTemplate.queryForObject(SELECT_STATEMENT, new SavedStatementRowMapper(statementServiceLineItems, statementMerchandiseLineItems, statementSpecialChargeLineItems, statementCashAdvanceLineItems), id);
    }

    /**
     * @param request
     *
     * @return
     */
    @Transactional
    @Override
    public SavedStatement insertSavedStatement(StatementRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        wfhBillingJdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS);

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
            return preparedStatement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (Objects.isNull(key)) {
            throw new IllegalStateException("Insert succeeded but no generated key was returned");
        }

        int savedStatementId = key.intValue();

        insertLineItems(request, savedStatementId);

        return findById(savedStatementId);
    }

    /**
     * @param id
     * @param request
     *
     * @return
     */
    @Transactional
    @Override
    public SavedStatement update(int id, StatementRequest request) {
        Integer rowCount = wfhBillingJdbcTemplate.queryForObject("SELECT COUNT(*) FROM saved_statements WHERE id = ?", Integer.class, id);

        if (Objects.nonNull(rowCount) && !rowCount.equals(1)) {
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
                id
        );

        wfhBillingJdbcTemplate.update(DELETE_SERVICE, id);
        wfhBillingJdbcTemplate.update(DELETE_MERCHANDISE, id);
        wfhBillingJdbcTemplate.update(DELETE_SPECIAL_CHARGE, id);
        wfhBillingJdbcTemplate.update(DELETE_CASH_ADVANCES, id);

        insertLineItems(request, id);

        return findById(id);
    }

    private void insertLineItems(StatementRequest request, int savedStatementId) {
        wfhBillingJdbcTemplate.batchUpdate(INSERT_SERVICE, request.services(), request.services().size(), (preparedStatement, item) -> {
            preparedStatement.setInt(1, savedStatementId);
            preparedStatement.setInt(2, item.serviceId());
            preparedStatement.setBoolean(3, item.inPackage());
        });

        wfhBillingJdbcTemplate.batchUpdate(INSERT_MERCHANDISE, request.merchandise(), request.merchandise().size(), (preparedStatement, item) -> {
            preparedStatement.setInt(1, savedStatementId);
            preparedStatement.setInt(2, item.merchandiseId());
            preparedStatement.setBigDecimal(3, item.price());
            preparedStatement.setString(4, item.description());
        });

        wfhBillingJdbcTemplate.batchUpdate(INSERT_CASH_ADVANCE, request.cashAdvances(), request.cashAdvances().size(), (preparedStatement, item) -> {
            preparedStatement.setInt(1, savedStatementId);
            preparedStatement.setInt(2, item.cashAdvanceId());
            preparedStatement.setBigDecimal(3, item.amount());
            preparedStatement.setString(4, item.provider());
        });

        wfhBillingJdbcTemplate.batchUpdate(INSERT_SPECIAL_CHARGE, request.specialCharges(), request.specialCharges().size(), (preparedStatement, item) -> {
            preparedStatement.setInt(1, savedStatementId);
            preparedStatement.setInt(2, item.specialChargeId());
            preparedStatement.setBigDecimal(3, item.price());
            preparedStatement.setString(4, item.description());
        });
    }

    /**
     * @return
     */
    @Override
    public int nextControlNumber() {
        Integer max = wfhBillingJdbcTemplate.queryForObject(SELECT_MAX_CONTROL_NUMBER, Integer.class);
        return max == null ? 1 : max + 1;
    }
}
