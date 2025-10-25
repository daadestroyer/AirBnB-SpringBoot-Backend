package com.org.AirBnB.dto;

import lombok.*;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class HotelSearchRequest {
    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;
    private Integer pageNumber=0;
    private Integer pageSize=5;
}
