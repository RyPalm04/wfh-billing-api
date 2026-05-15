package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.SavedStatement;
import com.palmer.wfhbillingapi.model.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.StatementSpecialChargeLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SavedStatementRowMapper implements RowMapper<SavedStatement> {

    private final List<StatementServiceLineItem> services;
    private final List<StatementMerchandiseLineItem> merchandise;
    private final List<StatementSpecialChargeLineItem> specialCharges;
    private final List<StatementCashAdvanceLineItem> cashAdvances;

    public SavedStatementRowMapper(List<StatementServiceLineItem> services, List<StatementMerchandiseLineItem> merchandise,
                                   List<StatementSpecialChargeLineItem> specialCharges, List<StatementCashAdvanceLineItem> cashAdvances) {
        this.services = services;
        this.merchandise = merchandise;
        this.specialCharges = specialCharges;
        this.cashAdvances = cashAdvances;
    }

    /**
     * @param rs
     *         the {@code ResultSet} to map (pre-initialized for the current row)
     * @param rowNum
     *         the number of the current row
     *
     * @return
     *
     * @throws SQLException
     */
    @Override
    public SavedStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SavedStatement(
                rs.getInt("id"),
                rs.getInt("control_number"),
                rs.getString("services_for_name"),
                rs.getObject("date_of_death", LocalDate.class),
                rs.getString("place_of_death"),
                rs.getObject("service_date", LocalDate.class),
                rs.getString("reason_for_embalming"),
                (Integer) rs.getObject("package_id"),
                rs.getBigDecimal("sales_tax_rate"),
                rs.getBigDecimal("payment"),
                rs.getObject("saved_at", LocalDateTime.class),
                services,
                merchandise,
                specialCharges,
                cashAdvances
        );
    }
}
