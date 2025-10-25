package com.org.AirBnB.dto;

import com.org.AirBnB.entities.Booking;
import com.org.AirBnB.entities.User;
import com.org.AirBnB.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {
    private Long guestId;
    private User user;
    private String guestName;
    private Gender gender;
    private Integer age;
}
