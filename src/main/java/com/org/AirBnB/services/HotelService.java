package com.org.AirBnB.services;

import com.org.AirBnB.dto.HotelDTO;

import java.util.List;

public interface HotelService {
    HotelDTO createHotel(HotelDTO hotelDTO);
    HotelDTO getHotelById(Long hotelId);
    List<HotelDTO> getAllHotel();
    HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDto);
    void deleteHotelById(Long id);
    void activateHotel(Long hotelId);

    String deActivateHotel(Long hotelId);
}
