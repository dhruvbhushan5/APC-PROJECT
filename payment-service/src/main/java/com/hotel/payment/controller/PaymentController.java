package com.hotel.payment.controller;

import com.hotel.payment.service.PaymentService;
import com.hotel.common.dto.ApiResponse;
import com.hotel.common.dto.PaymentDto;
import com.hotel.common.dto.PaymentMethod;
import com.hotel.common.dto.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Payment management operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Payment Management", description = "APIs for managing hotel payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Process a payment", description = "Processes a new payment for a booking")
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @Valid @RequestBody PaymentDto paymentDto) {
        log.info("Processing payment for booking: {} with amount: {}", 
                paymentDto.getBookingId(), paymentDto.getAmount());
        
        PaymentDto processedPayment = paymentService.processPayment(paymentDto);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment processed successfully")
                .data(processedPayment)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id) {
        log.info("Fetching payment with ID: {}", id);
        
        PaymentDto payment = paymentService.getPaymentById(id);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(payment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment by transaction ID", description = "Retrieves a payment by transaction ID")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable @NotBlank String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        
        PaymentDto payment = paymentService.getPaymentByTransactionId(transactionId);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(payment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all payments", description = "Retrieves all payments with pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getAllPayments(
            @PageableDefault(size = 20, sort = "paymentDate") Pageable pageable) {
        log.info("Fetching all payments with pagination: {}", pageable);
        
        Page<PaymentDto> payments = paymentService.getAllPayments(pageable);
        ApiResponse<Page<PaymentDto>> response = ApiResponse.<Page<PaymentDto>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payments by booking", description = "Retrieves all payments for a specific booking")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByBookingId(
            @Parameter(description = "Booking ID") @PathVariable @Positive Long bookingId) {
        log.info("Fetching payments for booking: {}", bookingId);
        
        List<PaymentDto> payments = paymentService.getPaymentsByBookingId(bookingId);
        ApiResponse<List<PaymentDto>> response = ApiResponse.<List<PaymentDto>>builder()
                .success(true)
                .message("Booking payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get successful payments by booking", description = "Retrieves successful payments for a booking")
    @GetMapping("/booking/{bookingId}/successful")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getSuccessfulPaymentsByBookingId(
            @Parameter(description = "Booking ID") @PathVariable @Positive Long bookingId) {
        log.info("Fetching successful payments for booking: {}", bookingId);
        
        List<PaymentDto> payments = paymentService.getSuccessfulPaymentsByBookingId(bookingId);
        ApiResponse<List<PaymentDto>> response = ApiResponse.<List<PaymentDto>>builder()
                .success(true)
                .message("Successful payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payments by status", description = "Retrieves payments filtered by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByStatus(
            @Parameter(description = "Payment status") @PathVariable PaymentStatus status) {
        log.info("Fetching payments by status: {}", status);
        
        List<PaymentDto> payments = paymentService.getPaymentsByStatus(status);
        ApiResponse<List<PaymentDto>> response = ApiResponse.<List<PaymentDto>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payments by method", description = "Retrieves payments filtered by payment method")
    @GetMapping("/method/{method}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByMethod(
            @Parameter(description = "Payment method") @PathVariable PaymentMethod method) {
        log.info("Fetching payments by method: {}", method);
        
        List<PaymentDto> payments = paymentService.getPaymentsByMethod(method);
        ApiResponse<List<PaymentDto>> response = ApiResponse.<List<PaymentDto>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payments by customer", description = "Retrieves payments for a specific customer")
    @GetMapping("/customer/{email}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByCustomerEmail(
            @Parameter(description = "Customer email") @PathVariable @Email String email) {
        log.info("Fetching payments for customer: {}", email);
        
        List<PaymentDto> payments = paymentService.getPaymentsByCustomerEmail(email);
        ApiResponse<List<PaymentDto>> response = ApiResponse.<List<PaymentDto>>builder()
                .success(true)
                .message("Customer payments retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get customer payment history", description = "Retrieves payment history for a customer with pagination")
    @GetMapping("/customer/{email}/history")
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> getCustomerPaymentHistory(
            @Parameter(description = "Customer email") @PathVariable @Email String email,
            @PageableDefault(size = 20, sort = "paymentDate") Pageable pageable) {
        log.info("Fetching payment history for customer: {}", email);
        
        Page<PaymentDto> payments = paymentService.getCustomerPaymentHistory(email, pageable);
        ApiResponse<Page<PaymentDto>> response = ApiResponse.<Page<PaymentDto>>builder()
                .success(true)
                .message("Customer payment history retrieved successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get total paid amount for booking", description = "Calculates total paid amount for a booking")
    @GetMapping("/booking/{bookingId}/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaidAmountForBooking(
            @Parameter(description = "Booking ID") @PathVariable @Positive Long bookingId) {
        log.info("Calculating total paid amount for booking: {}", bookingId);
        
        BigDecimal totalAmount = paymentService.getTotalPaidAmountForBooking(bookingId);
        ApiResponse<BigDecimal> response = ApiResponse.<BigDecimal>builder()
                .success(true)
                .message("Total paid amount calculated successfully")
                .data(totalAmount)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search payments", description = "Search payments with multiple filters")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> searchPayments(
            @Parameter(description = "Booking ID") @RequestParam(required = false) Long bookingId,
            @Parameter(description = "Payment status") @RequestParam(required = false) PaymentStatus status,
            @Parameter(description = "Payment method") @RequestParam(required = false) PaymentMethod paymentMethod,
            @Parameter(description = "Customer email") @RequestParam(required = false) @Email String customerEmail,
            @Parameter(description = "Minimum amount") @RequestParam(required = false) BigDecimal minAmount,
            @Parameter(description = "Maximum amount") @RequestParam(required = false) BigDecimal maxAmount,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "paymentDate") Pageable pageable) {
        log.info("Searching payments with filters");
        
        Page<PaymentDto> payments = paymentService.searchPayments(
                bookingId, status, paymentMethod, customerEmail, minAmount, maxAmount, startDate, endDate, pageable);
        ApiResponse<Page<PaymentDto>> response = ApiResponse.<Page<PaymentDto>>builder()
                .success(true)
                .message("Payment search completed successfully")
                .data(payments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refund payment", description = "Processes a full refund for a payment")
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentDto>> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id,
            @Parameter(description = "Refund amount") @RequestParam BigDecimal refundAmount,
            @Parameter(description = "Refund reason") @RequestParam(required = false) String refundReason) {
        log.info("Processing refund for payment: {} with amount: {}", id, refundAmount);
        
        PaymentDto refundedPayment = paymentService.refundPayment(id, refundAmount, refundReason);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment refunded successfully")
                .data(refundedPayment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Partial refund payment", description = "Processes a partial refund for a payment")
    @PostMapping("/{id}/partial-refund")
    public ResponseEntity<ApiResponse<PaymentDto>> partialRefundPayment(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id,
            @Parameter(description = "Refund amount") @RequestParam BigDecimal refundAmount,
            @Parameter(description = "Refund reason") @RequestParam(required = false) String refundReason) {
        log.info("Processing partial refund for payment: {} with amount: {}", id, refundAmount);
        
        PaymentDto refundedPayment = paymentService.partialRefundPayment(id, refundAmount, refundReason);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Partial refund processed successfully")
                .data(refundedPayment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel payment", description = "Cancels a pending payment")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PaymentDto>> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String cancellationReason) {
        log.info("Cancelling payment: {} with reason: {}", id, cancellationReason);
        
        PaymentDto cancelledPayment = paymentService.cancelPayment(id, cancellationReason);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment cancelled successfully")
                .data(cancelledPayment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retry payment", description = "Retries a failed payment")
    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<PaymentDto>> retryPayment(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id) {
        log.info("Retrying payment: {}", id);
        
        PaymentDto retriedPayment = paymentService.retryPayment(id);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment retry completed")
                .data(retriedPayment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update payment status", description = "Updates the status of a payment")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentDto>> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable @Positive Long id,
            @Parameter(description = "New payment status") @RequestParam PaymentStatus status,
            @Parameter(description = "Gateway response") @RequestParam(required = false) String gatewayResponse) {
        log.info("Updating payment {} status to: {}", id, status);
        
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(id, status, gatewayResponse);
        ApiResponse<PaymentDto> response = ApiResponse.<PaymentDto>builder()
                .success(true)
                .message("Payment status updated successfully")
                .data(updatedPayment)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get total revenue", description = "Gets total revenue for a period")
    @GetMapping("/revenue/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching total revenue for period: {} to {}", startDate, endDate);
        
        BigDecimal totalRevenue = paymentService.getTotalRevenue(startDate, endDate);
        ApiResponse<BigDecimal> response = ApiResponse.<BigDecimal>builder()
                .success(true)
                .message("Total revenue retrieved successfully")
                .data(totalRevenue)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get revenue by payment method", description = "Gets revenue breakdown by payment method")
    @GetMapping("/revenue/by-method")
    public ResponseEntity<ApiResponse<Map<PaymentMethod, BigDecimal>>> getRevenueByPaymentMethod(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching revenue by payment method for period");
        
        Map<PaymentMethod, BigDecimal> revenue = paymentService.getRevenueByPaymentMethod(startDate, endDate);
        ApiResponse<Map<PaymentMethod, BigDecimal>> response = ApiResponse.<Map<PaymentMethod, BigDecimal>>builder()
                .success(true)
                .message("Revenue by payment method retrieved successfully")
                .data(revenue)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment statistics by status", description = "Gets payment statistics by status")
    @GetMapping("/stats/by-status")
    public ResponseEntity<ApiResponse<Map<PaymentStatus, Long>>> getPaymentStatsByStatus(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching payment statistics by status");
        
        Map<PaymentStatus, Long> stats = paymentService.getPaymentStatsByStatus(startDate, endDate);
        ApiResponse<Map<PaymentStatus, Long>> response = ApiResponse.<Map<PaymentStatus, Long>>builder()
                .success(true)
                .message("Payment statistics by status retrieved successfully")
                .data(stats)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment statistics by method", description = "Gets payment statistics by method")
    @GetMapping("/stats/by-method")
    public ResponseEntity<ApiResponse<Map<PaymentMethod, Long>>> getPaymentStatsByMethod(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching payment statistics by method");
        
        Map<PaymentMethod, Long> stats = paymentService.getPaymentStatsByMethod(startDate, endDate);
        ApiResponse<Map<PaymentMethod, Long>> response = ApiResponse.<Map<PaymentMethod, Long>>builder()
                .success(true)
                .message("Payment statistics by method retrieved successfully")
                .data(stats)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
