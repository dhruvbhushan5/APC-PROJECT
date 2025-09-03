package com.hotel.room.entity;

import com.hotel.common.dto.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_guest_email", columnList = "guestEmail"),
    @Index(name = "idx_booking_dates", columnList = "checkInDate, checkOutDate"),
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_room", columnList = "room_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Room room;
    
    @Column(nullable = false)
    private Long guestId; // Reference to User service (future implementation)
    
    @Column(nullable = false, length = 100)
    private String guestName;
    
    @Column(nullable = false, length = 100)
    private String guestEmail;
    
    @Column(length = 20)
    private String guestPhone;
    
    @Column(nullable = false)
    private LocalDate checkInDate;
    
    @Column(nullable = false)
    private LocalDate checkOutDate;
    
    @Column(nullable = false)
    private Integer numberOfNights;
    
    @Column(nullable = false)
    private Integer numberOfGuests;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    
    @Column(length = 1000)
    private String specialRequests;
    
    @Column(length = 500)
    private String cancellationReason;
    
    @Column
    private LocalDateTime checkInTime;
    
    @Column
    private LocalDateTime checkOutTime;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; // For optimistic locking
    
    // Calculated fields (can be computed from check-in/check-out dates)
    @PrePersist
    @PreUpdate
    private void calculateNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            this.numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        }
    }
}
