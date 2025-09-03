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
public class PaymentDto {
    
    private Long id;
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
    
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;
    
    @Size(max = 500, message = "Payment description must not exceed 500 characters")
    private String description;
    
    private LocalDateTime paymentDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Additional fields for payment gateway integration
    private String gatewayResponse;
    
    private String gatewayTransactionId;
}
