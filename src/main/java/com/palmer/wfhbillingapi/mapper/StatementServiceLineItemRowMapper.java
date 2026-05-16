package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a {@code saved_statement_services} result set row to a {@link StatementServiceLineItem}.
 */
public class StatementServiceLineItemRowMapper implements RowMapper<StatementServiceLineItem> {

    @Override
    public StatementServiceLineItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StatementServiceLineItem(
            rs.getInt("service_id"),
            rs.getBoolean("in_package")
        );
    }
}
