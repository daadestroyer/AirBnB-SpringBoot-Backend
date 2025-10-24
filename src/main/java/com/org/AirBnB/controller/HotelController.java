package com.org.AirBnB.controller;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@Slf4j
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody HotelDTO hotelDTO) {
        HotelDTO hotel = hotelService.createHotel(hotelDTO);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotelById(@PathVariable Long hotelId) {
        HotelDTO hotelById = hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelById, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllHotels() {
        List<HotelDTO> allHotel = hotelService.getAllHotel();
        return new ResponseEntity<>(allHotel, HttpStatus.OK);

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
    public ResponseEntity<?> deActivateHotel(@PathVariable Long hotelId){
        String message = hotelService.deActivateHotel(hotelId);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
}
