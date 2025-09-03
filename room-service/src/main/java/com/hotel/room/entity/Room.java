package com.hotel.room.entity;

import com.hotel.common.dto.RoomType;
import com.hotel.common.dto.RoomStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_number", columnList = "roomNumber", unique = true),
    @Index(name = "idx_room_type", columnList = "roomType"),
    @Index(name = "idx_room_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(length = 1000)
    private String amenities;
    
    @Column(nullable = false)
    private Integer floorNumber;
    
    @Column(length = 50)
    private String view; // Ocean view, City view, Garden view, etc.
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean smokingAllowed = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean petFriendly = false;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal area; // Room area in square meters
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; // For optimistic locking
}
