package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.model.tenant.Tenant;
import com.palmer.wfhbillingapi.repository.TenantRepository;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StripeWebhookServiceImpl implements StripeWebhookService {
    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookServiceImpl.class);

    private final TenantRepository tenantRepository;
    private final TenantBootstrapService tenantBootstrapService;
    private final SupabaseAdminService supabaseAdminService;
    private final DesktopAuthService desktopAuthService;

    public StripeWebhookServiceImpl(TenantRepository tenantRepository, TenantBootstrapService tenantBootstrapService,
                                    SupabaseAdminService supabaseAdminService, DesktopAuthService desktopAuthService) {
        this.tenantRepository = tenantRepository;
        this.tenantBootstrapService = tenantBootstrapService;
        this.supabaseAdminService = supabaseAdminService;
        this.desktopAuthService = desktopAuthService;
    }

    @Transactional
    @Override
    public void handleEvent(Event event) {
        switch (event.getType()) {
            case "customer.subscription.created" -> handleSubscriptionCreated(event);
            case "invoice.payment_failed" -> handlePaymentFailed(event);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
            default -> logger.info("Unhandled Stripe event of type: {}", event.getType());
        }
    }

    private void handleSubscriptionCreated(Event event) {
        logger.debug("handleSubscriptionCreated called with event {}", event);
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
        String supabaseUserId = subscription.getMetadata().get("supabase_user_id");
        UUID tenantId = UUID.randomUUID();

        tenantRepository.save(new Tenant(tenantId, null, "active", LocalDateTime.now(),
                supabaseUserId, subscription.getCustomer(), subscription.getId()));

        tenantBootstrapService.seedTenantTables(tenantId);

        supabaseAdminService.updateUserTenantMetadata(supabaseUserId, tenantId);

        desktopAuthService.generateLicenseKey(tenantId);
    }

    private void handlePaymentFailed(Event event) {
        logger.debug("handlePaymentFailed called with event {}", event);
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
        tenantRepository.updateStatusByStripeCustomerId("past_due", invoice.getCustomer());
    }

    private void handleSubscriptionDeleted(Event event) {
        logger.debug("handleSubscriptionDeleted called with event {}", event);
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
        tenantRepository.updateStatusByStripeCustomerId("canceled", subscription.getCustomer());
    }
}
