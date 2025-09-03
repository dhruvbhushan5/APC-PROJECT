package com.hotel.payment.service.impl;

import com.hotel.payment.entity.Payment;
import com.hotel.payment.mapper.PaymentMapper;
import com.hotel.payment.repository.PaymentRepository;
import com.hotel.payment.service.PaymentService;
import com.hotel.common.dto.PaymentDto;
import com.hotel.common.dto.PaymentMethod;
import com.hotel.common.dto.PaymentStatus;
import com.hotel.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentService for payment processing operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    
    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        log.info("Processing payment for booking: {} with amount: {}", 
                paymentDto.getBookingId(), paymentDto.getAmount());
        
        // Validate payment amount
        if (paymentDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        Payment payment = paymentMapper.toEntity(paymentDto);
        
        // Generate transaction ID if not provided
        if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
            payment.setTransactionId(generateTransactionId());
        }
        
        // Set initial status
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Simulate payment processing
        try {
            processPaymentGateway(payment);
            payment.setStatus(PaymentStatus.COMPLETED);
            log.info("Payment processed successfully for booking: {}", paymentDto.getBookingId());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            log.error("Payment processing failed for booking: {}", paymentDto.getBookingId(), e);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long paymentId) {
        log.debug("Fetching payment with ID: {}", paymentId);
        Payment payment = findPaymentById(paymentId);
        return paymentMapper.toDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByTransactionId(String transactionId) {
        log.debug("Fetching payment with transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment with transaction ID", transactionId));
        return paymentMapper.toDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByGatewayTransactionId(String gatewayTransactionId) {
        log.debug("Fetching payment with gateway transaction ID: {}", gatewayTransactionId);
        Payment payment = paymentRepository.findByGatewayTransactionId(gatewayTransactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment with gateway transaction ID", gatewayTransactionId));
        return paymentMapper.toDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        log.debug("Fetching all payments with pagination: {}", pageable);
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(paymentMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByBookingId(Long bookingId) {
        log.debug("Fetching payments for booking: {}", bookingId);
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getSuccessfulPaymentsByBookingId(Long bookingId) {
        log.debug("Fetching successful payments for booking: {}", bookingId);
        List<Payment> payments = paymentRepository.findSuccessfulPaymentsByBookingId(bookingId);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByStatus(PaymentStatus status) {
        log.debug("Fetching payments with status: {}", status);
        List<Payment> payments = paymentRepository.findByStatus(status);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByMethod(PaymentMethod paymentMethod) {
        log.debug("Fetching payments with method: {}", paymentMethod);
        List<Payment> payments = paymentRepository.findByPaymentMethod(paymentMethod);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByCustomerEmail(String customerEmail) {
        log.debug("Fetching payments for customer: {}", customerEmail);
        List<Payment> payments = paymentRepository.findByCustomerEmail(customerEmail);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getCustomerPaymentHistory(String customerEmail, Pageable pageable) {
        log.debug("Fetching payment history for customer: {}", customerEmail);
        Page<Payment> payments = paymentRepository.findCustomerPaymentHistory(customerEmail, pageable);
        return payments.map(paymentMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmountForBooking(Long bookingId) {
        log.debug("Calculating total paid amount for booking: {}", bookingId);
        return paymentRepository.getTotalPaidAmountForBooking(bookingId);
    }
    
    @Override
    public PaymentDto refundPayment(Long paymentId, BigDecimal refundAmount, String refundReason) {
        log.info("Processing full refund for payment: {} with amount: {}", paymentId, refundAmount);
        
        Payment payment = findPaymentById(paymentId);
        
        if (!payment.isSuccessful()) {
            throw new IllegalStateException("Can only refund successful payments");
        }
        
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed original payment amount");
        }
        
        // Process refund through gateway
        try {
            processRefundGateway(payment, refundAmount);
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundAmount(refundAmount);
            payment.setRefundDate(LocalDateTime.now());
            payment.setRefundReason(refundReason);
            
            log.info("Refund processed successfully for payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Refund processing failed for payment: {}", paymentId, e);
            throw new RuntimeException("Refund processing failed: " + e.getMessage());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }
    
    @Override
    public PaymentDto partialRefundPayment(Long paymentId, BigDecimal refundAmount, String refundReason) {
        log.info("Processing partial refund for payment: {} with amount: {}", paymentId, refundAmount);
        
        Payment payment = findPaymentById(paymentId);
        
        if (!payment.isSuccessful()) {
            throw new IllegalStateException("Can only refund successful payments");
        }
        
        BigDecimal currentRefundAmount = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal totalRefundAmount = currentRefundAmount.add(refundAmount);
        
        if (totalRefundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Total refund amount cannot exceed original payment amount");
        }
        
        // Process partial refund through gateway
        try {
            processRefundGateway(payment, refundAmount);
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            payment.setRefundAmount(totalRefundAmount);
            payment.setRefundDate(LocalDateTime.now());
            payment.setRefundReason(refundReason);
            
            log.info("Partial refund processed successfully for payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Partial refund processing failed for payment: {}", paymentId, e);
            throw new RuntimeException("Partial refund processing failed: " + e.getMessage());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }
    
    @Override
    public PaymentDto cancelPayment(Long paymentId, String cancellationReason) {
        log.info("Cancelling payment: {} with reason: {}", paymentId, cancellationReason);
        
        Payment payment = findPaymentById(paymentId);
        
        if (!payment.isPending()) {
            throw new IllegalStateException("Can only cancel pending payments");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(cancellationReason);
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Successfully cancelled payment: {}", paymentId);
        return paymentMapper.toDto(updatedPayment);
    }
    
    @Override
    public PaymentDto retryPayment(Long paymentId) {
        log.info("Retrying payment: {}", paymentId);
        
        Payment payment = findPaymentById(paymentId);
        
        if (!payment.isFailed()) {
            throw new IllegalStateException("Can only retry failed payments");
        }
        
        // Reset payment status and try again
        payment.setStatus(PaymentStatus.PENDING);
        payment.setFailureReason(null);
        payment.setFailureCode(null);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Simulate payment retry
        try {
            processPaymentGateway(payment);
            payment.setStatus(PaymentStatus.COMPLETED);
            log.info("Payment retry successful for payment: {}", paymentId);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            log.error("Payment retry failed for payment: {}", paymentId, e);
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getStalePayments(LocalDateTime cutoffTime) {
        log.debug("Fetching stale payments before: {}", cutoffTime);
        List<Payment> payments = paymentRepository.findStalePayments(cutoffTime);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getFailedPayments(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching failed payments between: {} and {}", startDate, endDate);
        List<Payment> payments = paymentRepository.findFailedPaymentsBetween(startDate, endDate);
        return paymentMapper.toDtoList(payments);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> searchPayments(Long bookingId, PaymentStatus status, PaymentMethod paymentMethod,
                                          String customerEmail, BigDecimal minAmount, BigDecimal maxAmount,
                                          LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Searching payments with filters");
        Page<Payment> payments = paymentRepository.searchPayments(
            bookingId, status, paymentMethod, customerEmail, minAmount, maxAmount, startDate, endDate, pageable);
        return payments.map(paymentMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching total revenue for period: {} to {}", startDate, endDate);
        return paymentRepository.getTotalRevenueForPeriod(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching revenue by payment method for period");
        List<Object[]> stats = paymentRepository.getRevenueByPaymentMethod(startDate, endDate);
        return stats.stream()
            .collect(Collectors.toMap(
                stat -> (PaymentMethod) stat[0],
                stat -> (BigDecimal) stat[1]
            ));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<PaymentStatus, Long> getPaymentStatsByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching payment statistics by status");
        List<Object[]> stats = paymentRepository.getPaymentStatsByStatus(startDate, endDate);
        return stats.stream()
            .collect(Collectors.toMap(
                stat -> (PaymentStatus) stat[0],
                stat -> (Long) stat[1]
            ));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<PaymentMethod, Long> getPaymentStatsByMethod(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching payment statistics by method");
        List<Object[]> stats = paymentRepository.getPaymentStatsByMethod(startDate, endDate);
        return stats.stream()
            .collect(Collectors.toMap(
                stat -> (PaymentMethod) stat[0],
                stat -> (Long) stat[1]
            ));
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRefundAmount(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching total refund amount for period");
        return paymentRepository.getTotalRefundAmountForPeriod(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getRecentFailedPaymentCount(String customerEmail, LocalDateTime timeWindow) {
        log.debug("Checking recent failed payments for customer: {}", customerEmail);
        return paymentRepository.countRecentFailedPaymentsByEmail(customerEmail, timeWindow);
    }
    
    @Override
    public PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status, String gatewayResponse) {
        log.info("Updating payment {} status to: {}", paymentId, status);
        
        Payment payment = findPaymentById(paymentId);
        payment.setStatus(status);
        if (gatewayResponse != null) {
            payment.setGatewayResponse(gatewayResponse);
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Successfully updated payment {} status to: {}", paymentId, status);
        return paymentMapper.toDto(updatedPayment);
    }
    
    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void processPaymentGateway(Payment payment) {
        // Simulate payment gateway processing
        // In a real implementation, this would integrate with actual payment gateways
        
        log.debug("Processing payment through gateway for amount: {}", payment.getAmount());
        
        // Simulate random failure for demonstration (10% failure rate)
        if (Math.random() < 0.1) {
            throw new RuntimeException("Payment gateway timeout");
        }
        
        // Set gateway information
        payment.setGatewayTransactionId("GTW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setGatewayName("MockGateway");
        payment.setGatewayStatus("SUCCESS");
        payment.setGatewayResponse("Payment processed successfully");
        
        log.debug("Payment processed successfully through gateway");
    }
    
    private void processRefundGateway(Payment payment, BigDecimal refundAmount) {
        // Simulate refund gateway processing
        log.debug("Processing refund through gateway for amount: {}", refundAmount);
        
        // In a real implementation, this would integrate with actual payment gateways
        // For now, we'll just simulate success
        
        log.debug("Refund processed successfully through gateway");
    }
}
