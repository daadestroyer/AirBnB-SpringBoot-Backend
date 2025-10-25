package com.org.AirBnB.repository;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.Inventory;
import com.org.AirBnB.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);


    /*
     Reason for using dateCount
     ----------------------------
       When a user searches for hotels between checkInDate and checkOutDate,
       you don’t just want any hotel that has availability on some days —
       you want hotels that have rooms available for every single day in that range.
       If the user searches Jan 1 → Jan 3,
        ✅ Hotel B qualifies (available all 3 days)
        ❌ Hotel A does not (unavailable on Jan 3)
     */
    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount) >= :roomsCount
           GROUP BY i.hotel, i.room
           HAVING COUNT(i.date) = :dateCount
           """)
    Page<Hotel> findAvailableHotelsWithGivenParameters(
            @Param("city") String city,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );
}
