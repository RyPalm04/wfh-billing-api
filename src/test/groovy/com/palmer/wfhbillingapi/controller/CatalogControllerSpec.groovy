package com.palmer.wfhbillingapi.controller

import com.palmer.wfhbillingapi.model.*
import com.palmer.wfhbillingapi.repository.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CatalogControllerSpec extends Specification {

    def servicePackageRepository = Mock(ServicePackageRepository)
    def serviceRepository = Mock(ServiceRepository)
    def merchandiseRepository = Mock(MerchandiseRepository)
    def specialChargeRepository = Mock(SpecialChargeRepository)
    def cashAdvanceRepository = Mock(CashAdvanceRepository)

    MockMvc mockMvc

    def setup() {
        def controller = new CatalogController(
                cashAdvanceRepository,
                merchandiseRepository,
                serviceRepository,
                servicePackageRepository,
                specialChargeRepository,
        )
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    def "GET /catalog/cash-advances returns 200 with results"() {
        given:
        cashAdvanceRepository.findAll() >> [new CashAdvance(1, 1, "Grave Opening", true)]

        when:
        def result = mockMvc.perform(get("/catalog/cash-advances"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/merchandise returns 200 with results"() {
        given:
        merchandiseRepository.findAll() >> [new Merchandise(1, 1, "Casket or (alternative container)", null, true, true, Merchandise.PricingMode.FLAT)]

        when:
        def result = mockMvc.perform(get("/catalog/merchandise"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/services returns 200 with results"() {
        given:
        serviceRepository.findAll() >> [new Service(1, "Basic Services of Funeral Director & Staff", 1, 1895.00G, true)]

        when:
        def result = mockMvc.perform(get("/catalog/services"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/packages returns 200 with results"() {
        given:
        servicePackageRepository.findAll() >> [new ServicePackage(1, 1, "Traditional One", 5995.00G)]

        when:
        def result = mockMvc.perform(get("/catalog/packages"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/special-charges returns 200 with results"() {
        given:
        specialChargeRepository.findAll() >> [new SpecialCharge(1, 1, "Grave Service Setup/Delivery", null, true)]

        when:
        def result = mockMvc.perform(get("/catalog/special-charges"))

        then:
        result.andExpect(status().isOk())
    }
}
