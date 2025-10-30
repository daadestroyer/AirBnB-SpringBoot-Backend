package com.org.AirBnB.strategy;

import com.org.AirBnB.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
