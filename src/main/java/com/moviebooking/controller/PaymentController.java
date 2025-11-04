package com.moviebooking.controller;

import com.moviebooking.dto.PaymentIntentRequest;
import com.moviebooking.dto.PaymentIntentResponse;
import com.moviebooking.exception.PaymentProcessingException;
import com.moviebooking.service.IPaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@Valid @RequestBody PaymentIntentRequest request) {
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Invalid payment intent request: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (PaymentProcessingException ex) {
            LOGGER.error("Payment processing error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
