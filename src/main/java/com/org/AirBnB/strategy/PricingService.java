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

    // Calculating dynamic pricing for each day inventory
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }
}
