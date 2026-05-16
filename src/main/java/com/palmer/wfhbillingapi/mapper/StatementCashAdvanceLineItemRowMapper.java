package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a {@code saved_statement_cash_advances} result set row to a {@link StatementCashAdvanceLineItem}.
 */
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
