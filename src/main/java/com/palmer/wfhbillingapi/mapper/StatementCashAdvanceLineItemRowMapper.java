package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.StatementCashAdvanceLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementCashAdvanceLineItemRowMapper implements RowMapper<StatementCashAdvanceLineItem> {

    @Override
    public StatementCashAdvanceLineItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StatementCashAdvanceLineItem(
            rs.getInt("cash_advance_id"),
            rs.getBigDecimal("amount"),
            rs.getString("provider")
        );
    }
}
