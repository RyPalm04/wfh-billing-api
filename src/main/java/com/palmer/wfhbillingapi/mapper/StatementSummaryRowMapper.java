package com.palmer.wfhbillingapi.mapper;

import com.palmer.wfhbillingapi.model.statement.StatementSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps a {@code saved_statements} result set row to a {@link StatementSummary}.
 * Reads only the five header columns needed for list views.
 */
public class StatementSummaryRowMapper implements RowMapper<StatementSummary> {

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
