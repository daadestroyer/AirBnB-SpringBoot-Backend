package com.org.AirBnB.services;

import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.HotelMinPrice;
import com.org.AirBnB.entities.Inventory;
import com.org.AirBnB.repository.HotelMinPriceRepository;
import com.org.AirBnB.repository.HotelRepository;
import com.org.AirBnB.repository.InventoryRepository;
import com.org.AirBnB.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    // Scheduler to update the inventory and HotelMinPrice tables every hour
//    @Scheduled(cron = "0 0 * * * *")
//    @Scheduled(cron = "*/5 * * * * *")
    public void updatePrices() {
        // go hotel by hotel and update the data
        int page = 0;
        int pageSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, pageSize));
            if (hotelPage.isEmpty()) {
                break;
            }
            // update hotel for each price
            hotelPage.getContent().forEach(hotel -> updateHotelPrice(hotel));
            page++;
        }
    }

    private void updateHotelPrice(Hotel hotel) {
        log.info("Updating hotel prices for hotel id {} ",hotel.getHotelId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        updateInventoryPrice(inventoryList);
        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // for this hotel create a list of 365 days and on each day we will have a value as price[days act as key and value as price]
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList
                .stream()
                .collect(Collectors.groupingBy(
                        // all the inventories belong to this date will group together
                        Inventory::getDate,
                        // For each date group, we extract prices (Inventory::getPrice) and then find the minimum price among them
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // prepare HotelPrices entities in bulk
        ArrayList<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date).orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setMinPrice(price);
            hotelPrices.add(hotelMinPrice);
        });
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrice(List<Inventory> inventoryList) {
        log.info("Started updated inventory pricing");
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryList);
        log.info("Inventory pricing update complete");
    }


}
