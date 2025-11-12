package com.org.AirBnB.strategy;

import com.org.AirBnB.entities.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasicPricing();

        pricingStrategy = new PricingSkeleton(pricingStrategy);
        pricingStrategy = new OccupancyPricingDecorator(pricingStrategy);
        pricingStrategy = new UrgencyPricingDecorator(pricingStrategy);
        pricingStrategy = new HolidayPricingDecorator(pricingStrategy);



        return pricingStrategy.calculatePrice(inventory);

    }
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
