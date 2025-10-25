package com.org.AirBnB.services.Impl;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.entities.*;
import com.org.AirBnB.entities.enums.BookingStatus;
import com.org.AirBnB.exception.InventoryNotAvailableException;
import com.org.AirBnB.exception.ResourceNotFoundException;
import com.org.AirBnB.repository.BookingRepository;
import com.org.AirBnB.repository.HotelRepository;
import com.org.AirBnB.repository.InventoryRepository;
import com.org.AirBnB.repository.RoomRepository;
import com.org.AirBnB.services.BookingService;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDTO createMyBooking(BookingRequest bookingRequest) {

        // 1. Fetch the hotel first
        Hotel hotel = hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id = " + bookingRequest.getHotelId() + " not found"));

        // 2. Fetch the rooms
        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with id = " + bookingRequest.getRoomId() + " not found"));

        // 3. Check if inventory is available in user provided checkInDate and checkOutDate
        List<Inventory> lockedAvailableInventory = inventoryRepository
                .findAndLockAvailableInventory(
                        bookingRequest.getRoomId(),
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate(),
                        bookingRequest.getRoomsCount()
                );

        // 4. check if room count is available in particular date range
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (lockedAvailableInventory.size() != daysCount) {
            throw new InventoryNotAvailableException("Room is not available with your provided date range");
        }

        // 5. Reserve room for 3 minutes, so no other person can book also hold the price for 3 minutes
        for (Inventory inventory : lockedAvailableInventory) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(lockedAvailableInventory);

        // 6. Create the booking

        // TODO : Remove dummy user
        User user = User.builder().userId(101L).build();

        // TODO : calculate dynamic price
        BigDecimal amount = BigDecimal.TEN;
        Booking booking = Booking
                .builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .roomCount(bookingRequest.getRoomsCount())
                .amount(amount).build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking,BookingDTO.class);
    }

    @Override
    public String deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
        return "Booking "+bookingId+" deleted...";
    }
}
