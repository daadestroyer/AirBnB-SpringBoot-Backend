package com.org.AirBnB.services;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.HotelInfoDTO;

import java.util.List;

public interface HotelService {
    HotelDTO createHotel(HotelDTO hotelDTO);
    HotelInfoDTO getHotelById(Long hotelId);
    List<HotelDTO> getAllHotel();
    HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDto);
    void deleteHotelById(Long id);
    void activateHotel(Long hotelId);

    String deActivateHotel(Long hotelId);
}
