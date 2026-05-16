package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a {@code saved_statement_special_charges} result set row to a {@link StatementSpecialChargeLineItem}.
 */
public class StatementSpecialChargeLineItemRowMapper implements RowMapper<StatementSpecialChargeLineItem> {

    @Override
    public StatementSpecialChargeLineItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StatementSpecialChargeLineItem(
            rs.getInt("special_charge_id"),
            rs.getBigDecimal("price"),
            rs.getString("description")
        );
    }
}
