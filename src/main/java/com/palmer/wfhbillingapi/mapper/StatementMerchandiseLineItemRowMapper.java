package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.StatementMerchandiseLineItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementMerchandiseLineItemRowMapper implements RowMapper<StatementMerchandiseLineItem> {

    @Override
    public StatementMerchandiseLineItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StatementMerchandiseLineItem(
            rs.getInt("merchandise_id"),
            rs.getBigDecimal("price"),
            rs.getString("description")
        );
    }
}
