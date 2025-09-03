package com.hotel.room.controller;

import com.hotel.room.entity.Booking;
import com.hotel.room.entity.Room;
import com.hotel.room.repository.BookingRepository;
import com.hotel.room.repository.RoomRepository;
import com.hotel.common.dto.BookingStatus;
import com.hotel.common.dto.RoomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SimpleBookingController {
    
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
        log.info("Creating booking for room {} from {} to {}", 
            request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        
        try {
            // Find the room
            Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
            
            // Create booking
            long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(
                request.getCheckInDate(), request.getCheckOutDate());
            
            Booking booking = Booking.builder()
                .room(room)
                .guestId(1L) // Default guest ID for demo
                .guestEmail(request.getGuestEmail())
                .guestName(request.getGuestName())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(1) // Default to 1 guest
                .numberOfNights((int) numberOfNights)
                .totalAmount(calculateTotalAmount(room.getPricePerNight(), 
                    request.getCheckInDate(), request.getCheckOutDate()))
                .paidAmount(BigDecimal.ZERO) // Not paid yet
                .status(BookingStatus.PENDING) // Pending payment
                .guestPhone(request.getPhoneNumber())
                .specialRequests(request.getSpecialRequests())
                .build();
            
            // Save booking
            Booking savedBooking = bookingRepository.save(booking);
            
            // Update room status to reserved (temporarily)
            room.setStatus(RoomStatus.RESERVED);
            roomRepository.save(room);
            
            log.info("Booking created successfully with ID: {}", savedBooking.getId());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", savedBooking.getId());
            responseData.put("confirmationNumber", "HTL" + savedBooking.getId() + 
                String.valueOf((int)(Math.random() * 10000)));
            responseData.put("status", "PENDING_PAYMENT");
            responseData.put("totalAmount", savedBooking.getTotalAmount());
            responseData.put("paidAmount", savedBooking.getPaidAmount());
            responseData.put("roomNumber", room.getRoomNumber());
            responseData.put("roomType", room.getRoomType().toString());
            responseData.put("checkInDate", savedBooking.getCheckInDate());
            responseData.put("checkOutDate", savedBooking.getCheckOutDate());
            responseData.put("guestName", savedBooking.getGuestName());
            responseData.put("paymentUrl", "http://localhost:3000/payment/" + savedBooking.getId());
            responseData.put("createdAt", LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseData);
            response.put("message", "Reservation created successfully. Please complete payment to confirm your booking.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        log.info("Fetching all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        log.info("Updating booking {} status to: {}", id, request.getStatus());
        
        try {
            Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Update booking status
            BookingStatus newStatus = BookingStatus.valueOf(request.getStatus());
            booking.setStatus(newStatus);
            
            // If payment is confirmed, mark as paid and confirm room reservation
            if (newStatus == BookingStatus.CONFIRMED) {
                booking.setPaidAmount(booking.getTotalAmount());
                
                // Update room status to occupied for confirmed bookings
                Room room = booking.getRoom();
                room.setStatus(RoomStatus.OCCUPIED);
                roomRepository.save(room);
            }
            
            Booking updatedBooking = bookingRepository.save(booking);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("data", updatedBooking);
            successResponse.put("message", "Booking status updated successfully");
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            log.error("Error updating booking status: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update booking status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    private BigDecimal calculateTotalAmount(BigDecimal pricePerNight, LocalDate checkIn, LocalDate checkOut) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }
    
        // Request DTO for creating bookings
    public static class CreateBookingRequest {
        private Long roomId;
        private String guestEmail;
        private String guestName;
        private String phoneNumber;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private String specialRequests;
        
        // Getters and setters
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public String getGuestEmail() { return guestEmail; }
        public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
        
        public String getGuestName() { return guestName; }
        public void setGuestName(String guestName) { this.guestName = guestName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
        
        public LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
        
        public String getSpecialRequests() { return specialRequests; }
        public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    }
    
    // Request DTO for updating booking status
    public static class UpdateStatusRequest {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
