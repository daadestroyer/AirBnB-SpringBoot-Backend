package com.org.AirBnB.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long hotelId;
    @Column(nullable = false)
    private String name;
    private String city;
    @Column(columnDefinition = "TEXT[]")
    private String[] photos;
    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Embedded
    private HotelContactInfo hotelContactInfo;
    @Column(nullable = false)
    private Boolean active;
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Room> rooms;
    @ManyToOne
    @JoinColumn(name = "hotel_owner_id")
    private User hotelOwner;
}
