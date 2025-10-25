package com.org.AirBnB.entities;

import com.org.AirBnB.entities.enums.BookingStatus;
import com.org.AirBnB.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String guestName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

}
