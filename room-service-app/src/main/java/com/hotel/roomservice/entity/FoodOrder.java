package com.hotel.roomservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "food_orders", indexes = {
    @Index(name = "idx_order_room", columnList = "roomNumber"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_booking", columnList = "bookingId"),
    @Index(name = "idx_order_date", columnList = "orderDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrder {
    
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
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<OrderItem> orderItems;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryFee;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(length = 500)
    private String specialInstructions;
    
    @Column
    private LocalDateTime requestedDeliveryTime;
    
    @Column
    private LocalDateTime estimatedDeliveryTime;
    
    @Column
    private LocalDateTime actualDeliveryTime;
    
    @Column(length = 100)
    private String deliveryStaff;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PREPARING,
        READY_FOR_DELIVERY,
        OUT_FOR_DELIVERY,
        DELIVERED,
        CANCELLED
    }
}
