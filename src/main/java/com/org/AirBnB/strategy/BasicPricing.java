package com.org.AirBnB.strategy;

import com.org.AirBnB.entities.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class BasicPricing implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
