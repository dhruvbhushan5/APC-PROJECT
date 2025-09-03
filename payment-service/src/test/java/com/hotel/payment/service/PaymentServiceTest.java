package com.hotel.payment.service;

import com.hotel.common.dto.PaymentDto;
import com.hotel.common.dto.PaymentStatus;
import com.hotel.common.dto.PaymentMethod;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.mapper.PaymentMapper;
import com.hotel.payment.repository.PaymentRepository;
import com.hotel.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .bookingId(100L)
                .amount(BigDecimal.valueOf(200.00))
                .currency("USD")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .transactionId("txn_123456789")
                .paymentGatewayReference("pg_ref_987654321")
                .description("Hotel room booking payment")
                .customerEmail("john.doe@example.com")
                .paymentDate(LocalDateTime.now())
                .build();

        paymentDto = PaymentDto.builder()
                .id(1L)
                .bookingId(100L)
                .amount(BigDecimal.valueOf(200.00))
                .currency("USD")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .transactionId("txn_123456789")
                .paymentGatewayReference("pg_ref_987654321")
                .description("Hotel room booking payment")
                .customerEmail("john.doe@example.com")
                .paymentDate(LocalDateTime.now())
                .build();
    }

    @Test
    void processPayment_ShouldCreatePayment_WhenValidInput() {
        // Given
        when(paymentMapper.toEntity(any(PaymentDto.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentDto);

        // When
        PaymentDto result = paymentService.processPayment(paymentDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(100L);
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(200.00));
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toEntity(any(PaymentDto.class));
        verify(paymentMapper).toDto(any(Payment.class));
    }

    @Test
    void getPaymentById_ShouldReturnPayment_WhenPaymentExists() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // When
        Optional<PaymentDto> result = paymentService.getPaymentById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTransactionId()).isEqualTo("txn_123456789");
        
        verify(paymentRepository).findById(1L);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void getPaymentById_ShouldReturnEmpty_WhenPaymentNotExists() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<PaymentDto> result = paymentService.getPaymentById(1L);

        // Then
        assertThat(result).isEmpty();
        verify(paymentRepository).findById(1L);
        verify(paymentMapper, never()).toDto(any(Payment.class));
    }

    @Test
    void getAllPayments_ShouldReturnPagedPayments() {
        // Given
        List<Payment> payments = Arrays.asList(payment);
        Page<Payment> paymentPage = new PageImpl<>(payments, PageRequest.of(0, 10), 1);
        List<PaymentDto> paymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payments)).thenReturn(paymentDtos);

        // When
        Page<PaymentDto> result = paymentService.getAllPayments(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTransactionId()).isEqualTo("txn_123456789");
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        verify(paymentRepository).findAll(any(Pageable.class));
        verify(paymentMapper).toDto(payments);
    }

    @Test
    void refundPayment_ShouldUpdatePaymentStatus_WhenPaymentExists() {
        // Given
        PaymentDto refundedDto = PaymentDto.builder()
                .id(1L)
                .bookingId(100L)
                .amount(BigDecimal.valueOf(200.00))
                .currency("USD")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.REFUNDED)
                .transactionId("txn_123456789")
                .paymentGatewayReference("pg_ref_987654321")
                .description("Hotel room booking payment")
                .customerEmail("john.doe@example.com")
                .paymentDate(LocalDateTime.now())
                .refundDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(refundedDto);

        // When
        Optional<PaymentDto> result = paymentService.refundPayment(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        
        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(any(Payment.class));
    }

    @Test
    void refundPayment_ShouldReturnEmpty_WhenPaymentNotExists() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<PaymentDto> result = paymentService.refundPayment(1L);

        // Then
        assertThat(result).isEmpty();
        verify(paymentRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void findPaymentsByBookingId_ShouldReturnPayments() {
        // Given
        List<Payment> bookingPayments = Arrays.asList(payment);
        List<PaymentDto> bookingPaymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findByBookingId(100L)).thenReturn(bookingPayments);
        when(paymentMapper.toDto(bookingPayments)).thenReturn(bookingPaymentDtos);

        // When
        List<PaymentDto> result = paymentService.findPaymentsByBookingId(100L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookingId()).isEqualTo(100L);
        
        verify(paymentRepository).findByBookingId(100L);
        verify(paymentMapper).toDto(bookingPayments);
    }

    @Test
    void findPaymentsByStatus_ShouldReturnPaymentsWithStatus() {
        // Given
        List<Payment> completedPayments = Arrays.asList(payment);
        List<PaymentDto> completedPaymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findByStatus(PaymentStatus.COMPLETED)).thenReturn(completedPayments);
        when(paymentMapper.toDto(completedPayments)).thenReturn(completedPaymentDtos);

        // When
        List<PaymentDto> result = paymentService.findPaymentsByStatus(PaymentStatus.COMPLETED);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        
        verify(paymentRepository).findByStatus(PaymentStatus.COMPLETED);
        verify(paymentMapper).toDto(completedPayments);
    }

    @Test
    void findPaymentsByCustomerEmail_ShouldReturnCustomerPayments() {
        // Given
        List<Payment> customerPayments = Arrays.asList(payment);
        List<PaymentDto> customerPaymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findByCustomerEmail("john.doe@example.com")).thenReturn(customerPayments);
        when(paymentMapper.toDto(customerPayments)).thenReturn(customerPaymentDtos);

        // When
        List<PaymentDto> result = paymentService.findPaymentsByCustomerEmail("john.doe@example.com");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerEmail()).isEqualTo("john.doe@example.com");
        
        verify(paymentRepository).findByCustomerEmail("john.doe@example.com");
        verify(paymentMapper).toDto(customerPayments);
    }

    @Test
    void findPaymentsByPaymentMethod_ShouldReturnPaymentsByMethod() {
        // Given
        List<Payment> creditCardPayments = Arrays.asList(payment);
        List<PaymentDto> creditCardPaymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findByPaymentMethod(PaymentMethod.CREDIT_CARD)).thenReturn(creditCardPayments);
        when(paymentMapper.toDto(creditCardPayments)).thenReturn(creditCardPaymentDtos);

        // When
        List<PaymentDto> result = paymentService.findPaymentsByPaymentMethod(PaymentMethod.CREDIT_CARD);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        
        verify(paymentRepository).findByPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(paymentMapper).toDto(creditCardPayments);
    }

    @Test
    void findPaymentsByDateRange_ShouldReturnPaymentsInDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Payment> dateRangePayments = Arrays.asList(payment);
        List<PaymentDto> dateRangePaymentDtos = Arrays.asList(paymentDto);
        
        when(paymentRepository.findByPaymentDateBetween(startDate, endDate)).thenReturn(dateRangePayments);
        when(paymentMapper.toDto(dateRangePayments)).thenReturn(dateRangePaymentDtos);

        // When
        List<PaymentDto> result = paymentService.findPaymentsByDateRange(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        
        verify(paymentRepository).findByPaymentDateBetween(startDate, endDate);
        verify(paymentMapper).toDto(dateRangePayments);
    }

    @Test
    void calculateTotalAmountByStatus_ShouldReturnCorrectSum() {
        // Given
        when(paymentRepository.calculateTotalAmountByStatus(PaymentStatus.COMPLETED))
                .thenReturn(BigDecimal.valueOf(500.00));

        // When
        BigDecimal result = paymentService.calculateTotalAmountByStatus(PaymentStatus.COMPLETED);

        // Then
        assertThat(result).isEqualTo(BigDecimal.valueOf(500.00));
        verify(paymentRepository).calculateTotalAmountByStatus(PaymentStatus.COMPLETED);
    }

    @Test
    void calculateTotalAmountByDateRange_ShouldReturnCorrectSum() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(paymentRepository.calculateTotalAmountByDateRange(startDate, endDate))
                .thenReturn(BigDecimal.valueOf(1500.00));

        // When
        BigDecimal result = paymentService.calculateTotalAmountByDateRange(startDate, endDate);

        // Then
        assertThat(result).isEqualTo(BigDecimal.valueOf(1500.00));
        verify(paymentRepository).calculateTotalAmountByDateRange(startDate, endDate);
    }

    @Test
    void countPaymentsByStatus_ShouldReturnCorrectCount() {
        // Given
        when(paymentRepository.countByStatus(PaymentStatus.COMPLETED)).thenReturn(10L);

        // When
        Long result = paymentService.countPaymentsByStatus(PaymentStatus.COMPLETED);

        // Then
        assertThat(result).isEqualTo(10L);
        verify(paymentRepository).countByStatus(PaymentStatus.COMPLETED);
    }

    @Test
    void findPaymentByTransactionId_ShouldReturnPayment_WhenTransactionExists() {
        // Given
        when(paymentRepository.findByTransactionId("txn_123456789")).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // When
        Optional<PaymentDto> result = paymentService.findPaymentByTransactionId("txn_123456789");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTransactionId()).isEqualTo("txn_123456789");
        
        verify(paymentRepository).findByTransactionId("txn_123456789");
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void findPaymentByTransactionId_ShouldReturnEmpty_WhenTransactionNotExists() {
        // Given
        when(paymentRepository.findByTransactionId("invalid_txn")).thenReturn(Optional.empty());

        // When
        Optional<PaymentDto> result = paymentService.findPaymentByTransactionId("invalid_txn");

        // Then
        assertThat(result).isEmpty();
        verify(paymentRepository).findByTransactionId("invalid_txn");
        verify(paymentMapper, never()).toDto(any(Payment.class));
    }
}
