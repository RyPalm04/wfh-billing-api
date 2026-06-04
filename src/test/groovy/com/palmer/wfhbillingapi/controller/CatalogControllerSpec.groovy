package com.palmer.wfhbillingapi.controller

import com.palmer.wfhbillingapi.model.catalog.*
import com.palmer.wfhbillingapi.repository.*
import com.palmer.wfhbillingapi.security.TenantContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
        TenantContext.setRole("platform_admin")
    }

    def cleanup() {
        TenantContext.clear()
    }

    def "GET /catalog/cash-advances returns 200 with results"() {
        given:
        cashAdvanceRepository.findAll() >> [new CashAdvance(1, 1, "Grave Opening", UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog/cash-advances"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/merchandise returns 200 with results"() {
        given:
        merchandiseRepository.findAll() >> [new Merchandise(1, 1, "Casket or (alternative container)", null, true, true, Merchandise.PricingMode.FLAT, UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog/merchandise"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/services returns 200 with results"() {
        given:
        serviceRepository.findAll() >> [new Service(1, "Basic Services of Funeral Director & Staff", 1, 1895.00G, false, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog/services"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/packages returns 200 with results"() {
        given:
        servicePackageRepository.findAll() >> [new ServicePackage(1, 1, "Traditional One", 5995.00G, false, UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog/packages"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/special-charges returns 200 with results"() {
        given:
        specialChargeRepository.findAll() >> [new SpecialCharge(1, 1, "Grave Service Setup/Delivery", null, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog/special-charges"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog returns 200 with all catalog items"() {
        given:
        servicePackageRepository.findAll() >> [new ServicePackage(1, 1, "Traditional One", 5995.00G, false, UUID.fromString("11111111-1111-1111-1111-111111111111"))]
        servicePackageRepository.findServiceIdsByPackageId(1) >> [1, 2, 3]
        cashAdvanceRepository.findAll() >> [new CashAdvance(1, 1, "Grave Opening", UUID.fromString("11111111-1111-1111-1111-111111111111"))]
        merchandiseRepository.findAll() >> [new Merchandise(1, 1, "Casket or (alternative container)", null, true, true, Merchandise.PricingMode.FLAT, UUID.fromString("11111111-1111-1111-1111-111111111111"))]
        serviceRepository.findAll() >> [new Service(1, "Basic Services of Funeral Director & Staff", 1, 1895.00G, false, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))]
        specialChargeRepository.findAll() >> [new SpecialCharge(1, 1, "Grave Service Setup/Delivery", null, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))]

        when:
        def result = mockMvc.perform(get("/catalog"))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /catalog/packages?includeLegacy=false excludes legacy packages"() {
        given:
        servicePackageRepository.findAll() >> [
                new ServicePackage(1, 1, "Traditional One", 5995.00G, false, UUID.fromString("11111111-1111-1111-1111-111111111111")),
                new ServicePackage(2, 2, "Legacy Package", 3000.00G, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))
        ]

        when:
        def result = mockMvc.perform(get("/catalog/packages")
                .param("includeLegacy", "false")
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$').isArray(),
                jsonPath('$.length()').value(1),
                jsonPath('$[0].name').value("Traditional One"))
    }

    def "GET /catalog/packages?includeLegacy=true returns all packages including legacy"() {
        given:
        servicePackageRepository.findAll() >> [
                new ServicePackage(1, 1, "Traditional One", 5995.00G, false, UUID.fromString("11111111-1111-1111-1111-111111111111")),
                new ServicePackage(2, 2, "Legacy Package", 3000.00G, true, UUID.fromString("11111111-1111-1111-1111-111111111111"))
        ]

        when:
        def result = mockMvc.perform(get("/catalog/packages")
                .param("includeLegacy", "true")
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$').isArray(),
                jsonPath('$.length()').value(2))
    }

    def "GET /catalog/packages only returns packages for the authenticated tenant"() {
        given:
        TenantContext.setTenantId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
        TenantContext.setRole("staff")
        servicePackageRepository.findAllByTenantId(UUID.fromString("11111111-1111-1111-1111-111111111111")) >> [
                new ServicePackage(1, 1, "Tenant Package", 5995.00G, false, UUID.fromString("11111111-1111-1111-1111-111111111111"))
        ]

        when:
        def result = mockMvc.perform(get("/catalog/packages")
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$.length()').value(1),
                jsonPath('$[0].name').value("Tenant Package"))
    }

    def "GET /catalog/packages never calls findAll() for a non-admin tenant"() {
        given:
        TenantContext.setTenantId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        TenantContext.setRole("staff")
        servicePackageRepository.findAllByTenantId(UUID.fromString("22222222-2222-2222-2222-222222222222")) >> []

        when:
        mockMvc.perform(get("/catalog/packages").accept(MediaType.APPLICATION_JSON))

        then:
        0 * servicePackageRepository.findAll()
    }

    def "GET /catalog/packages returns all tenants' data for platform admin"() {
        given:
        servicePackageRepository.findAll() >> [
                new ServicePackage(1, 1, "Tenant A Package", 5995.00G, false, UUID.fromString("22222222-2222-2222-2222-222222222222")),
                new ServicePackage(2, 2, "Tenant B Package", 4000.00G, false, UUID.fromString("33333333-3333-3333-3333-333333333333"))
        ]

        when:
        def result = mockMvc.perform(get("/catalog/packages").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpectAll(status().isOk(),
                jsonPath('$.length()').value(2))
        0 * servicePackageRepository.findAllByTenantId(_)
    }
}
