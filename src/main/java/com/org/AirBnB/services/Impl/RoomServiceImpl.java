package com.org.AirBnB.services.Impl;

import com.org.AirBnB.dto.RoomDTO;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.Room;
import com.org.AirBnB.entities.User;
import com.org.AirBnB.exception.customexceptions.HotelNotActiveException;
import com.org.AirBnB.exception.customexceptions.ResourceNotFoundException;
import com.org.AirBnB.exception.customexceptions.UnAuthorizedException;
import com.org.AirBnB.repository.HotelRepository;
import com.org.AirBnB.repository.RoomRepository;
import com.org.AirBnB.services.InventoryService;
import com.org.AirBnB.services.RoomService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private InventoryService inventoryService;

    @Override
    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO) {
        log.info("Creating new room in hotel with ID : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + hotelId + " not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getHotelOwner())){
            throw new UnAuthorizedException(user.getUserId()+" does not own this hotel "+hotelId);
        }

        if(!hotel.getActive()){
            throw new HotelNotActiveException("Before creating room in hotel "+hotelId+" activate the hotel first");
        }
        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);

        inventoryService.initializeRoomForYear(room);

        return modelMapper.map(savedRoom, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel");
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + hotelId + " not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getHotelOwner())){
            throw new UnAuthorizedException(user.getUserId()+" does not own this hotel "+hotelId);
        }

        return hotel
                .getRooms()
                .stream()
                .map((room) -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public RoomDTO getRoomById(Long roomId) {
        log.info("Getting room by id {} ", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId + " not found"));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room by id {} ", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId + " not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getHotelOwner())){
            throw new UnAuthorizedException(user.getUserId()+" does not own this room "+roomId);
        }
        // first delete all the future inventories for this particular room from today onwards
        // in below scenario we are doing two operations first deleting inventories and deleting room
        // inventories are associated with room
        // our call reaches here to delete inventories but not waited to complete deletion
        inventoryService.deleteAllInventory(room);

        // second delete the room
        // and move to delete rooms which causes
        // ERROR: update or delete on table "room" violates foreign key constraint "fkkkm00xm4as7hupix7tlg0q3of" on table "inventory"

        roomRepository.delete(room);
    }
}
