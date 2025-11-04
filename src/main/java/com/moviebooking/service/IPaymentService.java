package com.moviebooking.service;

import com.moviebooking.dto.PaymentIntentRequest;
import com.moviebooking.dto.PaymentIntentResponse;

public interface IPaymentService {

    PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request);
}
