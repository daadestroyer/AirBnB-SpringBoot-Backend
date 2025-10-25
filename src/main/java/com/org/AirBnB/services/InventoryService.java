package com.org.AirBnB.services;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.HotelSearchRequest;
import com.org.AirBnB.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForYear(Room room);

    void deleteAllInventory(Room room);

    Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest);
}
