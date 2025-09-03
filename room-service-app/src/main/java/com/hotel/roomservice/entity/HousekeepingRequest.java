package com.hotel.roomservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_requests", indexes = {
    @Index(name = "idx_hk_room", columnList = "roomNumber"),
    @Index(name = "idx_hk_status", columnList = "status"),
    @Index(name = "idx_hk_type", columnList = "requestType"),
    @Index(name = "idx_hk_priority", columnList = "priority"),
    @Index(name = "idx_hk_requested", columnList = "requestedAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousekeepingRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long bookingId; // Reference to booking
    
    @Column(nullable = false, length = 10)
    private String roomNumber;
    
    @Column(nullable = false, length = 100)
    private String guestName;
    
    @Column(length = 20)
    private String guestPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    
    @Column(nullable = false, length = 500)
    private String description;
    
    @Column(length = 1000)
    private String specialInstructions;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @Column
    private LocalDateTime preferredTime;
    
    @Column
    private LocalDateTime requestedTime;
    
    @Column
    private LocalDateTime scheduledTime;
    
    @Column
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime completedTime;
    
    @Column(length = 100)
    private String assignedStaff;
    
    @Column(length = 500)
    private String completionNotes;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum RequestType {
        CLEANING,
        MAINTENANCE,
        AMENITIES,
        LAUNDRY,
        SPECIAL_REQUEST
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
    
    public enum RequestStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
