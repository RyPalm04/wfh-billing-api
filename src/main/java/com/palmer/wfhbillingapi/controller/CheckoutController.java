package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.config.StripeProperties;
import com.palmer.wfhbillingapi.security.EternatelUserPrincipal;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.Mode;
import com.stripe.param.checkout.SessionCreateParams.SubscriptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private final StripeProperties stripeProperties;

    public CheckoutController(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostMapping("/checkout-session")
    public Map<String, String> createCheckoutSession(Authentication authentication) throws StripeException {
        EternatelUserPrincipal principal = (EternatelUserPrincipal) authentication.getPrincipal();
        logger.debug("EternatelUserPrincipal : {}", principal);

        SessionCreateParams params = SessionCreateParams.builder()
                                                        .setMode(Mode.SUBSCRIPTION)
                                                        .addLineItem(LineItem.builder()
                                                                             .setPrice(stripeProperties.priceId())
                                                                             .setQuantity(1L)
                                                                             .build())
                                                        .setSubscriptionData(SubscriptionData.builder()
                                                                                             .putMetadata("supabase_user_id", principal.userId())
                                                                                             .build())
                                                        .setCustomerEmail(principal.email())
                                                        .setSuccessUrl(stripeProperties.successUrl())
                                                        .setCancelUrl(stripeProperties.cancelUrl())
                                                        .build();
        logger.debug("SessionCreateParams : {}", params);

        Session session = Session.create(params);
        logger.debug("session : {}", session);

        return Map.of("url", session.getUrl());
    }
}
