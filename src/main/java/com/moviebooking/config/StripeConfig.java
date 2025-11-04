package com.moviebooking.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeConfig.class);

    private final String secretKey;

    public StripeConfig(@Value("${stripe.secret-key:}") String secretKey) {
        this.secretKey = secretKey;
    }

    @PostConstruct
    public void initializeStripe() {
        if (secretKey == null || secretKey.isBlank()) {
            LOGGER.warn("Stripe secret key is not configured. Payment endpoints will remain inactive until a key is provided.");
            return;
        }

        Stripe.apiKey = secretKey;
        LOGGER.info("Stripe API key initialized. Payments are enabled.");
    }
}
