package com.palmer.wfhbillingapi.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmer.wfhbillingapi.model.catalog.ServicePackage;
import com.palmer.wfhbillingapi.model.statement.SavedStatement;
import com.palmer.wfhbillingapi.model.lineitem.StatementCashAdvanceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementMerchandiseLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementServiceLineItem;
import com.palmer.wfhbillingapi.model.lineitem.StatementSpecialChargeLineItem;
import com.palmer.wfhbillingapi.model.statement.StatementData;
import com.palmer.wfhbillingapi.security.TenantContext;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Maps a {@code saved_statements} result set row to a {@link SavedStatement}. The four
 * line item collections are pre-fetched and injected via the constructor, since JDBC
 * does not support nested result sets — each collection requires a separate query before
 * the statement row is mapped.
 */
public class SavedStatementRowMapper implements RowMapper<SavedStatement> {

    private final ObjectMapper objectMapper;

    public SavedStatementRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Maps the current row of the {@code saved_statements} result set to a {@link SavedStatement},
     * attaching the pre-fetched line item lists supplied at construction time.
     *
     * @param rs     the {@code ResultSet} pre-positioned at the current row
     * @param rowNum the number of the current row
     * @return a fully populated {@link SavedStatement}
     * @throws SQLException if any column cannot be read
     */
    @Override
    public SavedStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
        String jsonData = rs.getString("data");
        StatementData data;

        try {
            data = objectMapper.readValue(jsonData, StatementData.class);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to deserialize statement data", e);
        }

        ServicePackage servicePackage = data.servicePackage();
        Boolean currentLegacy = (Boolean) rs.getObject("current_legacy_package");
        if (servicePackage != null && currentLegacy != null) {
            servicePackage = new ServicePackage(servicePackage.getId(), servicePackage.getSortOrder(), servicePackage.getName(), servicePackage.getDefaultCost(), currentLegacy, TenantContext.getTenantId());
        }
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
                servicePackage,
                data.services(),
                data.merchandise(),
                data.specialCharges(),
                data.cashAdvances()
        );
    }
}
