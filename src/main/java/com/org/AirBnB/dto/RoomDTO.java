package com.org.AirBnB.dto;

import com.org.AirBnB.entities.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {
    private Long roomId;

    private String roomType;

    private BigDecimal basePrice;

    private String[] photos;

    private String[] amenities;

    private Integer totalCount;

    private Integer capacity;

}
