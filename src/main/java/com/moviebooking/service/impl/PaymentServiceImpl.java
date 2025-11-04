package com.moviebooking.service.impl;

import com.moviebooking.dto.PaymentIntentRequest;
import com.moviebooking.dto.PaymentIntentResponse;
import com.moviebooking.exception.PaymentProcessingException;
import com.moviebooking.service.IPaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final String secretKey;
    private final String defaultCurrency;

    public PaymentServiceImpl(@Value("${stripe.secret-key:}") String secretKey,
            @Value("${stripe.currency:inr}") String defaultCurrency) {
        this.secretKey = secretKey;
        this.defaultCurrency = defaultCurrency;
    }

    @Override
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new PaymentProcessingException("Stripe secret key is not configured on the server");
        }

        long amount = request.getAmount();
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        String currency = request.getCurrency();
        if (currency == null || currency.isBlank()) {
            currency = defaultCurrency;
        }

        PaymentIntentCreateParams.AutomaticPaymentMethods automaticMethods
                = PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency.toLowerCase())
                .setAutomaticPaymentMethods(automaticMethods);

        if (request.getReceiptEmail() != null && !request.getReceiptEmail().isBlank()) {
            paramsBuilder.setReceiptEmail(request.getReceiptEmail());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            paramsBuilder.setDescription(request.getDescription());
        }

        try {
            if (Stripe.apiKey == null || Stripe.apiKey.isBlank()) {
                Stripe.apiKey = secretKey;
            }

            PaymentIntent intent = PaymentIntent.create(paramsBuilder.build());
            LOGGER.debug("Created Stripe payment intent {}", intent.getId());
            return new PaymentIntentResponse(intent.getClientSecret(), intent.getId());
        } catch (StripeException e) {
            LOGGER.error("Failed to create Stripe payment intent", e);
            throw new PaymentProcessingException("Unable to create payment intent", e);
        }
    }
}
