package com.org.AirBnB.controller;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.HotelSearchRequest;
import com.org.AirBnB.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<?> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelDTO> hotelDTOS = inventoryService.searchHotels(hotelSearchRequest);

        return new ResponseEntity<>(hotelDTOS, HttpStatus.OK);
    }
}
