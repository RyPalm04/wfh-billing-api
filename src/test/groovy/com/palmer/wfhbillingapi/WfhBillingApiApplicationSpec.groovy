package com.palmer.wfhbillingapi

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class WfhBillingApiApplicationSpec extends Specification {
    def "context loads"() {
        expect:
        true
    }
}