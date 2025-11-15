package com.org.AirBnB.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    // Avoid traversing hotel in toString/equals/hashCode too (optional but safe)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String roomType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @Column(nullable = false)
    private Integer totalCount;
    @Column(nullable = false)
    private Integer capacity;

    @CreationTimestamp
    @Column(unique = false,columnDefinition = "DATE")
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "DATE")
    private LocalDate updatedAt;

    @Column(nullable = false)
    private boolean active;

}
