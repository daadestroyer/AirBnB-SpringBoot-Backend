package com.org.AirBnB.services.Impl;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.Room;
import com.org.AirBnB.exception.NoRoomsFoundException;
import com.org.AirBnB.exception.ResourceNotFoundException;
import com.org.AirBnB.repository.HotelRepository;
import com.org.AirBnB.repository.RoomRepository;
import com.org.AirBnB.services.HotelService;
import com.org.AirBnB.services.InventoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating new hotel {} :", hotelDTO.getName());
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive(false);
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Created new hotel {} with ID {} :", hotelDTO.getName(), hotel.getHotelId());
        return modelMapper.map(savedHotel, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long hotelId) {
        log.info("Getting hotel with ID : {} ", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with not found with ID " + hotelId));
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public List<HotelDTO> getAllHotel() {
        List<Hotel> allHotels = hotelRepository.findAll();
        return allHotels
                .stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class)).collect(Collectors.toList());
    }

    @Override
    public HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDto) {
        log.info("Updating hotel with ID : {} ", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with not found with ID " + hotelId));

        // copy properties from DTO into the existing entity
        modelMapper.map(hotelDto, Hotel.class);

        // ensure id remains correct (optional if DTO doesn't carry id)
        hotel.setHotelId(hotelId);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long hotelId) {
        // first delete inventory
        // second delete room
        // then delete hotel
        log.info("Deleting hotel with ID : {} ", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with not found with ID " + hotelId));



        for(Room room : hotel.getRooms()){
            // delete all future inventories for this particular hotel
            inventoryService.deleteAllInventory(room);

            // delete room
            roomRepository.deleteById(room.getRoomId());
        }

        // delete the hotel
        hotelRepository.deleteById(hotelId);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating hotel with ID : {} ", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with not found with ID " + hotelId));

        hotel.setActive(true);
        hotelRepository.save(hotel);

//        for (Room room : hotel.getRooms()) {
//            inventoryService.initializeRoomForYear(room);
//        }
    }

    @Override
    public String deActivateHotel(Long hotelId) {
        log.info("De Activating hotel with ID : {} ", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with not found with ID " + hotelId));
        hotel.setActive(false);

        List<Room> rooms = hotel.getRooms();
        if(!rooms.isEmpty()){
            for(Room room : hotel.getRooms()){
                inventoryService.deleteAllInventory(room);
            }
        }

        return "Hotel De-Activated";
    }
}
