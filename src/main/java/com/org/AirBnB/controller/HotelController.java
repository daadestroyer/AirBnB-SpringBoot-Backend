package com.org.AirBnB.controller;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.HotelInfoDTO;
import com.org.AirBnB.dto.HotelReportDto;
import com.org.AirBnB.services.BookingService;
import com.org.AirBnB.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody HotelDTO hotelDTO) {
        HotelDTO hotel = hotelService.createHotel(hotelDTO);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotelById(@PathVariable Long hotelId) {
        HotelInfoDTO hotelById = hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelById, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllHotels() {
        List<HotelDTO> allHotel = hotelService.getAllHotel();
        return new ResponseEntity<>(allHotel, HttpStatus.OK);

    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<?> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsByHotelId(hotelId);
        return new ResponseEntity<>(bookingDTOList, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<?> updateHotelById(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDto) {
        HotelDTO hotel = hotelService.updateHotelById(hotelId, hotelDto);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotelById(@PathVariable Long hotelId) {
        hotelService.deleteHotelById(hotelId);
        return new ResponseEntity<>("Hotel " + hotelId + " deleted", HttpStatus.OK);
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<?> activateHotel(@PathVariable Long hotelId) {

        hotelService.activateHotel(hotelId);

        return new ResponseEntity<>("Hotel " + hotelId + " activated", HttpStatus.OK);
    }

    @GetMapping("/de-activate/{hotelId}")
    public ResponseEntity<?> deActivateHotel(@PathVariable Long hotelId) {
        String message = hotelService.deActivateHotel(hotelId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<?> getAllBookingsByHotelId(@PathVariable String hotelId,
                                                     @RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        log.info("Calling getHotelReport");
        HotelReportDto hotelReportDto = bookingService.getHotelReport(Long.valueOf(hotelId), startDate, endDate);
        return new ResponseEntity<>(hotelReportDto, HttpStatus.OK);
    }

}
