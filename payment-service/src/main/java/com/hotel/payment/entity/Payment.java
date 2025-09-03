package com.hotel.payment.entity;

import com.hotel.common.dto.PaymentMethod;
import com.hotel.common.dto.PaymentStatus;
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
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_booking", columnList = "bookingId"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_method", columnList = "paymentMethod"),
    @Index(name = "idx_payment_transaction", columnList = "transactionId"),
    @Index(name = "idx_payment_date", columnList = "paymentDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long bookingId; // Reference to booking in room-service
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(unique = true, length = 100)
    private String transactionId;
    
    @Column(length = 500)
    private String description;
    
    @Column
    private LocalDateTime paymentDate;
    
    // Payment gateway related fields
    @Column(length = 100)
    private String gatewayTransactionId;
    
    @Column(length = 50)
    private String gatewayName; // PayPal, Stripe, etc.
    
    @Column(length = 1000)
    private String gatewayResponse;
    
    @Column(length = 50)
    private String gatewayStatus;
    
    // Refund information
    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column
    private LocalDateTime refundDate;
    
    @Column(length = 500)
    private String refundReason;
    
    // Card information (for audit purposes - should be tokenized in real implementation)
    @Column(length = 20)
    private String cardLastFourDigits;
    
    @Column(length = 50)
    private String cardType; // Visa, MasterCard, etc.
    
    // Customer information
    @Column(length = 100)
    private String customerName;
    
    @Column(length = 100)
    private String customerEmail;
    
    // Failure information
    @Column(length = 100)
    private String failureCode;
    
    @Column(length = 500)
    private String failureReason;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; // For optimistic locking
    
    // Helper methods
    public boolean isSuccessful() {
        return PaymentStatus.COMPLETED.equals(this.status);
    }
    
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(this.status) || 
               PaymentStatus.PROCESSING.equals(this.status);
    }
    
    public boolean isFailed() {
        return PaymentStatus.FAILED.equals(this.status);
    }
    
    public boolean isRefunded() {
        return PaymentStatus.REFUNDED.equals(this.status) || 
               PaymentStatus.PARTIALLY_REFUNDED.equals(this.status);
    }
}
