package com.org.AirBnB.repository;

import com.org.AirBnB.entities.Booking;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.User;
import com.org.AirBnB.entities.enums.BookingStatus;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // find bookings with given status that were created before given threshold
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = :status AND b.createdAt < :threshold")
    List<Booking> findByStatusAndCreatedBefore(
            @Param("status") BookingStatus status,
            @Param("threshold") LocalDateTime threshold);


    Optional<Booking> findByStripePaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    @Query("SELECT b FROM Booking b WHERE b.hotel = :hotel AND b.createdAt BETWEEN :start AND :end")
    List<Booking> findByHotelAndCreatedAtBetween(@Param("hotel") Hotel hotel, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);



}
