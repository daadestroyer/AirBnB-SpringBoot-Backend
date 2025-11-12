package com.org.AirBnB.services;

import com.org.AirBnB.entities.Booking;
import com.stripe.exception.StripeException;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl) throws StripeException;
}
