package com.org.AirBnB.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
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
    @Column(columnDefinition = "DATE")
    private LocalDate createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "DATE")
    private LocalDate updatedAt;
    @Embedded
    private HotelContactInfo hotelContactInfo;
    @Column(nullable = false)
    private Boolean active;

    // Prevent Lombok toString/equals/hashCode from traversing rooms -> hotel -> rooms...
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Room> rooms;
    @ManyToOne
    @JoinColumn(name = "hotel_owner_id")
    private User hotelOwner;
}
