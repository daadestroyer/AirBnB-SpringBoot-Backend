package com.org.AirBnB.services.Impl;

import com.org.AirBnB.dto.HotelDTO;
import com.org.AirBnB.dto.HotelSearchRequest;
import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.Inventory;
import com.org.AirBnB.entities.Room;
import com.org.AirBnB.repository.InventoryRepository;
import com.org.AirBnB.services.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ModelMapper modelMapper;


    // Creating default inventory in advance for 1 year
    @Override
    public void initializeRoomForYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory
                    .builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(LocalDate.from(today.atStartOfDay()))
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);

        }

    }

    @Override
    public void deleteAllInventory(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        /*
            Criteria for search
            1. startDate <= date <= endDate
            2. city
            3. availability: (totalCount - bookedCount) >= roomCount

            >>> Our goal is to find the hotels which are matching our search values
            >>> We need to return hotel in paginated form
            >>> Group the response based on hotel
            >>>  and get the response by unique hotels

         */

        // this is the way to calculate total days between checkInDate and checkOutDate
        Long dateCount = (long) Math.toIntExact(ChronoUnit.DAYS.between(hotelSearchRequest.getCheckInDate(), hotelSearchRequest.getCheckOutDate()) + 1);


        PageRequest pageRequest = PageRequest.of(hotelSearchRequest.getPageNumber(), hotelSearchRequest.getPageSize());
        Page<Hotel> availableHotelsWithGivenParameters = inventoryRepository.findAvailableHotelsWithGivenParameters(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getCheckInDate(),
                hotelSearchRequest.getCheckOutDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageRequest
        );

        Page<HotelDTO> hotelDto = availableHotelsWithGivenParameters.map(hotel -> modelMapper.map(hotel, HotelDTO.class));

        return hotelDto;
    }
}
