package com.org.AirBnB.services;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;

public interface BookingService {

    BookingDTO createMyBooking(BookingRequest bookingRequest);

    String deleteBooking(Long bookingId);
}
