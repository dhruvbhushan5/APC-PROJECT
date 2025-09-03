package com.hotel.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    
    private Long id;
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Guest ID is required")
    private Long guestId;
    
    @NotBlank(message = "Guest name is required")
    @Size(max = 100, message = "Guest name must not exceed 100 characters")
    private String guestName;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Guest email is required")
    private String guestEmail;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;
    
    @NotNull(message = "Booking status is required")
    private com.hotel.common.dto.BookingStatus status;
    
    private String specialRequests;
    
    private Integer numberOfGuests;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Room information (for display purposes)
    private String roomNumber;
    private com.hotel.common.dto.RoomType roomType;
}
