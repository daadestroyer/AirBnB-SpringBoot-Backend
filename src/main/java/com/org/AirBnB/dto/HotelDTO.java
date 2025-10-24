package com.org.AirBnB.dto;

import com.org.AirBnB.entities.HotelContactInfo;
import com.org.AirBnB.entities.Room;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDTO {
    private long hotelId;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    @Embedded
    private HotelContactInfo hotelContactInfo;
    private Boolean active;
}
