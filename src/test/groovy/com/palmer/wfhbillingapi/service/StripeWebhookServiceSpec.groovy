package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.tenant.Tenant
import com.palmer.wfhbillingapi.repository.TenantRepository
import com.stripe.model.Event
import com.stripe.model.EventDataObjectDeserializer
import com.stripe.model.Invoice
import com.stripe.model.Subscription
import spock.lang.Specification

class StripeWebhookServiceSpec extends Specification {

    def tenantRepository = Mock(TenantRepository)
    def tenantBootstrapService = Mock(TenantBootstrapService)
    def supabaseAdminService = Mock(SupabaseAdminService)
    def desktopAuthService = Mock(DesktopAuthService)

    def service = new StripeWebhookServiceImpl(tenantRepository, tenantBootstrapService, supabaseAdminService, desktopAuthService)

    def "subscription.created saves tenant, seeds tables, and updates Supabase metadata"() {
        given:
        def subscription = Mock(Subscription)
        subscription.getMetadata() >> ["supabase_user_id": "user-abc"]
        subscription.getCustomer() >> "cus_123"
        subscription.getId() >> "sub_123"

        def deserializer = Mock(EventDataObjectDeserializer)
        deserializer.getObject() >> Optional.of(subscription)

        def event = Mock(Event)
        event.getType() >> "customer.subscription.created"
        event.getDataObjectDeserializer() >> deserializer

        when:
        service.handleEvent(event)

        then:
        1 * tenantRepository.save({ Tenant t ->
            t.status() == "active" &&
                    t.supabaseUserId() == "user-abc" &&
                    t.stripeCustomerId() == "cus_123" &&
                    t.stripeSubscriptionId() == "sub_123"
        })
        1 * tenantBootstrapService.seedTenantTables(_)
        1 * supabaseAdminService.updateUserTenantMetadata("user-abc", _)
        1 * desktopAuthService.generateLicenseKey(_)
    }

    def "invoice.payment_failed sets tenant status to past_due"() {
        given:
        def invoice = Mock(Invoice)
        invoice.getCustomer() >> "cus_123"

        def deserializer = Mock(EventDataObjectDeserializer)
        deserializer.getObject() >> Optional.of(invoice)

        def event = Mock(Event)
        event.getType() >> "invoice.payment_failed"
        event.getDataObjectDeserializer() >> deserializer

        when:
        service.handleEvent(event)

        then:
        1 * tenantRepository.updateStatusByStripeCustomerId("past_due", "cus_123")
    }

    def "subscription.deleted sets tenant status to canceled"() {
        given:
        def subscription = Mock(Subscription)
        subscription.getCustomer() >> "cus_123"

        def deserializer = Mock(EventDataObjectDeserializer)
        deserializer.getObject() >> Optional.of(subscription)

        def event = Mock(Event)
        event.getType() >> "customer.subscription.deleted"
        event.getDataObjectDeserializer() >> deserializer

        when:
        service.handleEvent(event)

        then:
        1 * tenantRepository.updateStatusByStripeCustomerId("canceled", "cus_123")
    }

    def "unrecognised event type is ignored"() {
        given:
        def event = Mock(Event)
        event.getType() >> "payment_intent.created"

        when:
        service.handleEvent(event)

        then:
        0 * tenantRepository._
        0 * tenantBootstrapService._
        0 * supabaseAdminService._
        0 * desktopAuthService._
    }
}
