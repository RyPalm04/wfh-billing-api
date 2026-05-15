package com.palmer.wfhbillingapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.palmer.wfhbillingapi.dto.StatementRequest
import com.palmer.wfhbillingapi.model.statement.SavedStatement
import com.palmer.wfhbillingapi.model.statement.StatementSummary
import com.palmer.wfhbillingapi.service.SavedStatementService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StatementControllerSpec extends Specification {

    SavedStatementService savedStatementService = Mock()
    MockMvc mockMvc
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StatementController(savedStatementService)).build()
    }

    def "GET /statements returns 200 with list of summaries"() {
        given:
        savedStatementService.findAll() >> [
            new StatementSummary(1, 1, "Test Person", LocalDate.of(2024, 1, 18), LocalDateTime.now())
        ]

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements"))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$').isArray())
              .andExpect(jsonPath('$[0].servicesForName').value("Test Person"))
    }

    def "GET /statements/{id} returns 200 with full statement"() {
        given:
        savedStatementService.findById(1) >> new SavedStatement(
            1, 1, "Test Person", null, "Memphis, TN",
            LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
            LocalDateTime.now(), [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/1"))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$.servicesForName').value("Test Person"))
    }

    def "POST /statements returns 201 with created statement"() {
        given:
        def request = new StatementRequest(1, "Test Person", null, "Memphis, TN",
            LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G, [], [], [], [])
        savedStatementService.insertSavedStatement(_) >> new SavedStatement(
            1, 1, "Test Person", null, "Memphis, TN",
            LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
            LocalDateTime.now(), [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/statements")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isCreated())
              .andExpect(jsonPath('$.id').value(1))
    }

    def "PUT /statements/{id} returns 200 with updated statement"() {
        given:
        def request = new StatementRequest(1, "Updated Person", null, "Memphis, TN",
            LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5500.00G, [], [], [], [])
        savedStatementService.update(1, _) >> new SavedStatement(
            1, 1, "Updated Person", null, "Memphis, TN",
            LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5500.00G,
            LocalDateTime.now(), [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.put("/statements/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$.servicesForName').value("Updated Person"))
    }

    def "GET /statements/next-control-number returns 200 with next number"() {
        given:
        savedStatementService.nextControlNumber() >> 5

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/next-control-number"))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$').value(5))
    }
}