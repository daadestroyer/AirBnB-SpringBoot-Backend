package com.org.AirBnB.repository;

import com.org.AirBnB.dto.HotelPriceDto;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {
    @Query("""
            SELECT new com.org.AirBnB.dto.HotelPriceDto(i.hotel, AVG(i.minPrice))
            FROM HotelMinPrice i
            WHERE i.hotel.city = :city
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND i.hotel.active = true
           GROUP BY i.hotel
           """)
    Page<HotelPriceDto> listAllMinPriceHotel(
            @Param("city") String city,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );
    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel,LocalDate date);
}
