package com.org.AirBnB.controller;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.entities.enums.BookingStatus;
import com.org.AirBnB.services.BookingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/booking")
@AllArgsConstructor
@NoArgsConstructor
public class BookingController {

    @Autowired
    private BookingService bookingService;

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
}
