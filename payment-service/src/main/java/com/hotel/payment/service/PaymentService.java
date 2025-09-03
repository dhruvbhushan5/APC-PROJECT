package com.hotel.payment.service;

import com.hotel.common.dto.PaymentDto;
import com.hotel.common.dto.PaymentMethod;
import com.hotel.common.dto.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Payment processing operations
 */
public interface PaymentService {
    
    /**
     * Process a new payment
     * @param paymentDto Payment information
     * @return Processed payment DTO
     */
    PaymentDto processPayment(PaymentDto paymentDto);
    
    /**
     * Get payment by ID
     * @param paymentId Payment ID
     * @return Payment DTO
     */
    PaymentDto getPaymentById(Long paymentId);
    
    /**
     * Get payment by transaction ID
     * @param transactionId Transaction ID
     * @return Payment DTO
     */
    PaymentDto getPaymentByTransactionId(String transactionId);
    
    /**
     * Get payment by gateway transaction ID
     * @param gatewayTransactionId Gateway transaction ID
     * @return Payment DTO
     */
    PaymentDto getPaymentByGatewayTransactionId(String gatewayTransactionId);
    
    /**
     * Get all payments with pagination
     * @param pageable Pagination information
     * @return Page of payment DTOs
     */
    Page<PaymentDto> getAllPayments(Pageable pageable);
    
    /**
     * Get payments by booking ID
     * @param bookingId Booking ID
     * @return List of payment DTOs
     */
    List<PaymentDto> getPaymentsByBookingId(Long bookingId);
    
    /**
     * Get successful payments for a booking
     * @param bookingId Booking ID
     * @return List of successful payment DTOs
     */
    List<PaymentDto> getSuccessfulPaymentsByBookingId(Long bookingId);
    
    /**
     * Get payments by status
     * @param status Payment status
     * @return List of payment DTOs
     */
    List<PaymentDto> getPaymentsByStatus(PaymentStatus status);
    
    /**
     * Get payments by payment method
     * @param paymentMethod Payment method
     * @return List of payment DTOs
     */
    List<PaymentDto> getPaymentsByMethod(PaymentMethod paymentMethod);
    
    /**
     * Get payments by customer email
     * @param customerEmail Customer email
     * @return List of payment DTOs
     */
    List<PaymentDto> getPaymentsByCustomerEmail(String customerEmail);
    
    /**
     * Get customer payment history with pagination
     * @param customerEmail Customer email
     * @param pageable Pagination information
     * @return Page of payment DTOs
     */
    Page<PaymentDto> getCustomerPaymentHistory(String customerEmail, Pageable pageable);
    
    /**
     * Get total paid amount for a booking
     * @param bookingId Booking ID
     * @return Total paid amount
     */
    BigDecimal getTotalPaidAmountForBooking(Long bookingId);
    
    /**
     * Refund a payment
     * @param paymentId Payment ID
     * @param refundAmount Refund amount
     * @param refundReason Reason for refund
     * @return Updated payment DTO
     */
    PaymentDto refundPayment(Long paymentId, BigDecimal refundAmount, String refundReason);
    
    /**
     * Partial refund a payment
     * @param paymentId Payment ID
     * @param refundAmount Partial refund amount
     * @param refundReason Reason for refund
     * @return Updated payment DTO
     */
    PaymentDto partialRefundPayment(Long paymentId, BigDecimal refundAmount, String refundReason);
    
    /**
     * Cancel a payment
     * @param paymentId Payment ID
     * @param cancellationReason Reason for cancellation
     * @return Updated payment DTO
     */
    PaymentDto cancelPayment(Long paymentId, String cancellationReason);
    
    /**
     * Retry a failed payment
     * @param paymentId Payment ID
     * @return Updated payment DTO
     */
    PaymentDto retryPayment(Long paymentId);
    
    /**
     * Get stale payments (pending too long)
     * @param cutoffTime Cutoff time for stale payments
     * @return List of stale payment DTOs
     */
    List<PaymentDto> getStalePayments(LocalDateTime cutoffTime);
    
    /**
     * Get failed payments within time range
     * @param startDate Start date
     * @param endDate End date
     * @return List of failed payment DTOs
     */
    List<PaymentDto> getFailedPayments(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Search payments with filters
     * @param bookingId Booking ID (optional)
     * @param status Payment status (optional)
     * @param paymentMethod Payment method (optional)
     * @param customerEmail Customer email (optional)
     * @param minAmount Minimum amount (optional)
     * @param maxAmount Maximum amount (optional)
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @param pageable Pagination information
     * @return Page of payment DTOs
     */
    Page<PaymentDto> searchPayments(Long bookingId, PaymentStatus status, PaymentMethod paymentMethod,
                                   String customerEmail, BigDecimal minAmount, BigDecimal maxAmount,
                                   LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Get total revenue for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get revenue by payment method for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Map of payment method to revenue
     */
    Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get payment statistics by status for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Map of status to count
     */
    Map<PaymentStatus, Long> getPaymentStatsByStatus(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get payment statistics by method for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Map of payment method to count
     */
    Map<PaymentMethod, Long> getPaymentStatsByMethod(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get total refund amount for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Total refund amount
     */
    BigDecimal getTotalRefundAmount(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Check for suspicious payment activity
     * @param customerEmail Customer email
     * @param timeWindow Time window to check
     * @return Number of recent failed payments
     */
    Long getRecentFailedPaymentCount(String customerEmail, LocalDateTime timeWindow);
    
    /**
     * Update payment status
     * @param paymentId Payment ID
     * @param status New payment status
     * @param gatewayResponse Gateway response (optional)
     * @return Updated payment DTO
     */
    PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status, String gatewayResponse);
}
