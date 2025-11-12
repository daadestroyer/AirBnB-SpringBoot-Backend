package com.org.AirBnB.services;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.dto.GuestDTO;
import com.org.AirBnB.entities.enums.BookingStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import jakarta.transaction.Transactional;

import java.util.List;

public interface BookingService {

    BookingDTO createMyBooking(BookingRequest bookingRequest);

    String deleteBooking(Long bookingId);

    BookingDTO addGuestToBooking(Long bookingId, List<GuestDTO> guestDTOList);

    String initiatePayment(Long bookingId) throws StripeException;


    void capturePayment(Event event);

    String cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);
}
