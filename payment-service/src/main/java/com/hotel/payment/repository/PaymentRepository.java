package com.hotel.payment.repository;

import com.hotel.payment.entity.Payment;
import com.hotel.common.dto.PaymentMethod;
import com.hotel.common.dto.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Basic finder methods
    List<Payment> findByBookingId(Long bookingId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    List<Payment> findByCustomerEmail(String customerEmail);
    
    // Successful payments for a booking
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.bookingId = :bookingId 
        AND p.status = 'COMPLETED'
        """)
    List<Payment> findSuccessfulPaymentsByBookingId(@Param("bookingId") Long bookingId);
    
    // Total paid amount for a booking
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0) FROM Payment p 
        WHERE p.bookingId = :bookingId 
        AND p.status = 'COMPLETED'
        """)
    BigDecimal getTotalPaidAmountForBooking(@Param("bookingId") Long bookingId);
    
    // Pending payments
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.status IN ('PENDING', 'PROCESSING') 
        AND p.createdAt < :cutoffTime
        """)
    List<Payment> findStalePayments(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Failed payments within time range
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.status = 'FAILED' 
        AND p.createdAt BETWEEN :startDate AND :endDate
        """)
    List<Payment> findFailedPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    // Payments by gateway
    @Query("SELECT p FROM Payment p WHERE p.gatewayName = :gatewayName")
    List<Payment> findByGatewayName(@Param("gatewayName") String gatewayName);
    
    // Revenue queries
    @Query("""
        SELECT SUM(p.amount) FROM Payment p 
        WHERE p.status = 'COMPLETED' 
        AND p.paymentDate BETWEEN :startDate AND :endDate
        """)
    BigDecimal getTotalRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("""
        SELECT p.paymentMethod, SUM(p.amount) FROM Payment p 
        WHERE p.status = 'COMPLETED' 
        AND p.paymentDate BETWEEN :startDate AND :endDate 
        GROUP BY p.paymentMethod
        """)
    List<Object[]> getRevenueByPaymentMethod(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    // Refund queries
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') 
        AND p.refundDate BETWEEN :startDate AND :endDate
        """)
    List<Payment> findRefundsBetween(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("""
        SELECT SUM(p.refundAmount) FROM Payment p 
        WHERE p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') 
        AND p.refundDate BETWEEN :startDate AND :endDate
        """)
    BigDecimal getTotalRefundAmountForPeriod(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    // Statistics
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);
    
    @Query("""
        SELECT p.status, COUNT(p) FROM Payment p 
        WHERE p.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY p.status
        """)
    List<Object[]> getPaymentStatsByStatus(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("""
        SELECT p.paymentMethod, COUNT(p) FROM Payment p 
        WHERE p.status = 'COMPLETED' 
        AND p.paymentDate BETWEEN :startDate AND :endDate 
        GROUP BY p.paymentMethod
        """)
    List<Object[]> getPaymentStatsByMethod(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    // Fraud detection queries
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.customerEmail = :email 
        AND p.status = 'FAILED' 
        AND p.createdAt > :since 
        ORDER BY p.createdAt DESC
        """)
    List<Payment> findRecentFailedPaymentsByEmail(@Param("email") String email,
                                                 @Param("since") LocalDateTime since);
    
    @Query("""
        SELECT COUNT(p) FROM Payment p 
        WHERE p.customerEmail = :email 
        AND p.status = 'FAILED' 
        AND p.createdAt > :since
        """)
    Long countRecentFailedPaymentsByEmail(@Param("email") String email,
                                         @Param("since") LocalDateTime since);
    
    // Payment history for customer
    @Query("""
        SELECT p FROM Payment p 
        WHERE p.customerEmail = :email 
        ORDER BY p.createdAt DESC
        """)
    Page<Payment> findCustomerPaymentHistory(@Param("email") String email, 
                                           Pageable pageable);
    
    // Search payments with filters
    @Query("""
        SELECT p FROM Payment p 
        WHERE (:bookingId IS NULL OR p.bookingId = :bookingId)
        AND (:status IS NULL OR p.status = :status)
        AND (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod)
        AND (:customerEmail IS NULL OR LOWER(p.customerEmail) LIKE LOWER(CONCAT('%', :customerEmail, '%')))
        AND (:minAmount IS NULL OR p.amount >= :minAmount)
        AND (:maxAmount IS NULL OR p.amount <= :maxAmount)
        AND (:startDate IS NULL OR p.createdAt >= :startDate)
        AND (:endDate IS NULL OR p.createdAt <= :endDate)
        ORDER BY p.createdAt DESC
        """)
    Page<Payment> searchPayments(@Param("bookingId") Long bookingId,
                                @Param("status") PaymentStatus status,
                                @Param("paymentMethod") PaymentMethod paymentMethod,
                                @Param("customerEmail") String customerEmail,
                                @Param("minAmount") BigDecimal minAmount,
                                @Param("maxAmount") BigDecimal maxAmount,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                Pageable pageable);
}
