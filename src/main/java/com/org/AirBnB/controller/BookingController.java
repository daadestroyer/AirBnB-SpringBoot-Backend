package com.org.AirBnB.controller;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.dto.BookingStatusResponseDto;
import com.org.AirBnB.dto.GuestDTO;
import com.org.AirBnB.entities.enums.BookingStatus;
import com.org.AirBnB.services.BookingService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest){
        BookingDTO myBooking = bookingService.createMyBooking(bookingRequest);
        return new ResponseEntity<>(myBooking, HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long bookingId){
        String message = bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PostMapping("/add-guest/{bookingId}")
    public ResponseEntity<?> addGuest(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        BookingDTO bookingDTO = bookingService.addGuestToBooking(bookingId, guestDTOList);
        return new ResponseEntity<>(bookingDTO,HttpStatus.OK);
    }
    @PostMapping("/initiate-payment/{bookingId}")
    public ResponseEntity<?> initiateBooking(@PathVariable Long bookingId) throws StripeException {
        String sessionUrl = bookingService.initiatePayment(bookingId);
        return new ResponseEntity<>(Map.of("sessionUrl",sessionUrl),HttpStatus.OK);
    }


    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel the booking", tags = {"Booking Flow"})
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        String message = bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @GetMapping("/{bookingId}/status")
    @Operation(summary = "Check the status of the booking", tags = {"Booking Flow"})
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(new BookingStatusResponseDto(bookingService.getBookingStatus(bookingId)));
    }
}
