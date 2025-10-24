package com.org.AirBnB.controller;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.RoomDTO;
import com.org.AirBnB.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/{hotelId}")
    public ResponseEntity<?> createRoom(@PathVariable Long hotelId, @RequestBody RoomDTO roomDTO) {
        RoomDTO roomDto = roomService.createNewRoom(hotelId, roomDTO);
        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

    @GetMapping("/get-room/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Long roomId) {
        RoomDTO roomDto = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomDto, HttpStatus.OK);
    }

    @GetMapping("/get-all-rooms/{hotelId}")
    public ResponseEntity<?> getAllRoomsInAHotel(@PathVariable Long hotelId) {
        List<RoomDTO> allRoomsInHotel = roomService.getAllRoomsInHotel(hotelId);
        return new ResponseEntity<>(allRoomsInHotel, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoomsById(@PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return new ResponseEntity<>("Room " + roomId + " deleted", HttpStatus.OK);
    }

}
