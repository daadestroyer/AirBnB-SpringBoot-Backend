package com.org.AirBnB.services.Impl;

import com.org.AirBnB.entities.Inventory;
import com.org.AirBnB.entities.Room;
import com.org.AirBnB.repository.InventoryRepository;
import com.org.AirBnB.services.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ModelMapper modelMapper;


    // Creating default inventory in advance for 1 year
    @Override
    public void initializeRoomForYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(;!today.isAfter(endDate);today = today.plusDays(1)){
            Inventory inventory = Inventory
                    .builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today.atStartOfDay())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);

        }

    }


    // from today onwards delete all future inventories for particular room
    @Override
    public void deleteAllInventory(Room room) {
        inventoryRepository.deleteByRoom(room);
    }
}
