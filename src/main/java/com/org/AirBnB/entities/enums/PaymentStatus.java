package com.org.AirBnB.entities.enums;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

public enum PaymentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
