package com.hotel.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    
    private Long id;
    
    @NotBlank(message = "Room number is required")
    @Size(max = 10, message = "Room number must not exceed 10 characters")
    private String roomNumber;
    
    @NotNull(message = "Room type is required")
    private RoomType roomType;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerNight;
    
    @NotNull(message = "Status is required")
    private RoomStatus status;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Integer capacity;
    
    private String amenities;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
