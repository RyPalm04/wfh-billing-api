package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.config.StripeProperties;
import com.palmer.wfhbillingapi.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {
    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);
    private final StripeWebhookService stripeWebhookService;
    private final StripeProperties stripeProperties;

    public StripeWebhookController(StripeWebhookService stripeWebhookService, StripeProperties stripeProperties) {
        this.stripeWebhookService = stripeWebhookService;
        this.stripeProperties = stripeProperties;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload,
                                              @RequestHeader("Stripe-Signature") String signatureHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, signatureHeader, stripeProperties.webhookSecret());
        } catch (SignatureVerificationException e) {
            logger.error("Could not construct webhook event", e);
            return ResponseEntity.badRequest().build();
        }

        stripeWebhookService.handleEvent(event);
        return ResponseEntity.ok().build();
    }
}
