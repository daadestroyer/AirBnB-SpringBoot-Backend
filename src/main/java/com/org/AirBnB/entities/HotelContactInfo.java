package com.org.AirBnB.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class HotelContactInfo {
    private String address;
    private String phoneNumber;
    private String pinCode;
    private String email;
}
