package com.org.AirBnB.dto;

import com.org.AirBnB.entities.*;
import com.org.AirBnB.entities.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Long bookingId;
    private HotelDTO hotel;
    private RoomDTO room;
    private UserDTO user;
    private Integer roomCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDTO> guestList;
}
