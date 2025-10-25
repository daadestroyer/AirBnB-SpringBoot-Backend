package com.org.AirBnB.entities.enums;

import jakarta.persistence.Entity;
import lombok.Data;


public enum BookingStatus {

    RESERVED,
    GUESTS_ADDED,
    PAYMENT_PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
