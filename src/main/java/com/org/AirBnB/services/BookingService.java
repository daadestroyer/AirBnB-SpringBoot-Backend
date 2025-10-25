package com.org.AirBnB.services;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.dto.GuestDTO;

import java.util.List;

public interface BookingService {

    BookingDTO createMyBooking(BookingRequest bookingRequest);

    String deleteBooking(Long bookingId);

    BookingDTO addGuestToBooking(Long bookingId, List<GuestDTO> guestDTOList);
}
