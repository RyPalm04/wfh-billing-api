package com.palmer.wfhbillingapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.palmer.wfhbillingapi.dto.StatementRequest
import com.palmer.wfhbillingapi.model.catalog.ServicePackage
import com.palmer.wfhbillingapi.model.statement.PdfResult
import com.palmer.wfhbillingapi.model.statement.SavedStatement
import com.palmer.wfhbillingapi.model.statement.StatementSummary
import com.palmer.wfhbillingapi.service.PdfService
import com.palmer.wfhbillingapi.service.SavedStatementService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class StatementControllerSpec extends Specification {

    SavedStatementService savedStatementService = Mock()
    PdfService pdfService = Mock()
    MockMvc mockMvc
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StatementController(savedStatementService, pdfService)).build()
    }

    def "GET /statements returns 200 with list of summaries"() {
        given:
        savedStatementService.findAll() >> [
                new StatementSummary(1, 1, "Test Person", LocalDate.of(2024, 1, 18), LocalDateTime.now())
        ]

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$[0].servicesForName').value("Test Person"))
    }

    def "GET /statements returns 200 with empty list"() {
        given:
        savedStatementService.findAll() >> []

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$').isEmpty())
    }

    def "GET /statements/{id} returns 200 with full statement"() {
        given:
        savedStatementService.findById(1) >> new SavedStatement(
                1, 1, "Test Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
                LocalDateTime.now(), null, [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/1").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.servicesForName').value("Test Person"))
    }

    def "POST /statements returns 201 with created statement"() {
        given:
        def request = new StatementRequest(1, "Test Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 0.0825G, 5000.00G, null, [], [], [], [])
        savedStatementService.insertSavedStatement(_) >> new SavedStatement(
                1, 1, "Test Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
                LocalDateTime.now(), null, [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/statements")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').value(1))
    }

    def "PUT /statements/{id} returns 200 with updated statement"() {
        given:
        def request = new StatementRequest(1, "Updated Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 0.0825G, 5500.00G, null, [], [], [], [])
        savedStatementService.update(1, _) >> new SavedStatement(
                1, 1, "Updated Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5500.00G,
                LocalDateTime.now(), null, [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.put("/statements/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.servicesForName').value("Updated Person"))
    }

    def "DELETE /statements/{id} returns 204"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.delete("/statements/1"))

        then:
        1 * savedStatementService.delete(1)
        result.andExpect(status().isNoContent())
    }


    def "GET /statements/next-control-number returns 200 with next number"() {
        given:
        savedStatementService.nextControlNumber() >> 5

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/next-control-number").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').value(5))
    }

    def "GET /statements/{id}/pdf returns 200 with PDF bytes"() {
        given:
        pdfService.generatePdf(1) >> new PdfResult(new byte[1], 1)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/1/pdf").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-1.pdf\""))
    }

    def "POST /statements with package name and price returns them in response"() {
        given:
        def request = new StatementRequest(1, "Test Person", null, "Memphis, TN",
                LocalDate.of(2024, 1, 18), "", 0.0825G, 5000.00G, new ServicePackage(1, 1, "Traditional Two", 7495.00G, false), [], [], [], [])

        savedStatementService.insertSavedStatement(_) >> new SavedStatement(
                1, 1, "Test Person", null, "Memphis, TN", LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
                LocalDateTime.now(), new ServicePackage(1, 1, "Traditional Two", 7495.00G, false), [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/statements")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpectAll(status().isCreated(),
                jsonPath('$.servicePackage.name').value("Traditional Two"),
                jsonPath('$.servicePackage.defaultCost').value(7495.0))
    }

    def "GET /statements/{id} returns packageName and packagePrice"() {
        given:
        savedStatementService.findById(1) >> new SavedStatement(
                1, 1, "Test Person", null, "Memphis, TN", LocalDate.of(2024, 1, 18), "", 1, 0.0825G, 5000.00G,
                LocalDateTime.now(), new ServicePackage(1, 1, "Traditional Two", 7495.00G, false), [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/1").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$.servicePackage.name').value("Traditional Two"),
                jsonPath('$.servicePackage.defaultCost').value(7495.0))
    }

    def "GET /statements/{id} returns servicePackage with legacy true"() {
        given:
        savedStatementService.findById(1) >> new SavedStatement(
                1, 1, "Test Person", null, "Memphis, TN", LocalDate.of(2024, 1, 18), "", 1,
                0.0825G, 5000.00G, LocalDateTime.now(), new ServicePackage(2, 2, "Legacy Package", 3000.00G, true),
                [], [], [], []
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/statements/1").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$.servicePackage.legacyPackage').value(true))
    }
}