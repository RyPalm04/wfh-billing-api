package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.StatementSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatementSummaryRowMapper implements RowMapper<StatementSummary> {

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
    public StatementSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.isBeforeFirst()) {
            return null;
        }

        return new StatementSummary(rs.getInt("id"), rs.getInt("control_number"),
                rs.getString("services_for_name"), rs.getObject("service_date", LocalDate.class),
                rs.getObject("saved_at", LocalDateTime.class));
    }
}
