package com.org.AirBnB.services;

import com.org.AirBnB.entities.Room;

public interface InventoryService {
    void initializeRoomForYear(Room room);

    void deleteAllInventory(Room room);

}
