package com.org.AirBnB.repository;

import com.org.AirBnB.entities.Inventory;
import com.org.AirBnB.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByRoom(Room room);
}
