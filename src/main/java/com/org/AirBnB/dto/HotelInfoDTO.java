package com.org.AirBnB.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class HotelInfoDTO {
    private HotelDTO hotelDTO;
    private List<RoomDTO> roomList;
}
