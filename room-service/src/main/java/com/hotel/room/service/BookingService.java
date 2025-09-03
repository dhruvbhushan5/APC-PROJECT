package com.hotel.room.service;

import com.hotel.common.dto.BookingDto;
import com.hotel.common.dto.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Booking management operations
 */
public interface BookingService {
    
    /**
     * Create a new booking
     * @param bookingDto Booking information
     * @return Created booking DTO
     */
    BookingDto createBooking(BookingDto bookingDto);
    
    /**
     * Update an existing booking
     * @param bookingId Booking ID
     * @param bookingDto Updated booking information
     * @return Updated booking DTO
     */
    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);
    
    /**
     * Get booking by ID
     * @param bookingId Booking ID
     * @return Booking DTO
     */
    BookingDto getBookingById(Long bookingId);
    
    /**
     * Get booking by ID and guest email (for security)
     * @param bookingId Booking ID
     * @param guestEmail Guest email
     * @return Booking DTO
     */
    BookingDto getBookingByIdAndEmail(Long bookingId, String guestEmail);
    
    /**
     * Get all bookings with pagination
     * @param pageable Pagination information
     * @return Page of booking DTOs
     */
    Page<BookingDto> getAllBookings(Pageable pageable);
    
    /**
     * Get bookings by guest email
     * @param guestEmail Guest email
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsByGuestEmail(String guestEmail);
    
    /**
     * Get bookings by guest ID
     * @param guestId Guest ID
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsByGuestId(Long guestId);
    
    /**
     * Get bookings by status
     * @param status Booking status
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsByStatus(BookingStatus status);
    
    /**
     * Get bookings for a specific room
     * @param roomId Room ID
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsByRoomId(Long roomId);
    
    /**
     * Get guest booking history with pagination
     * @param guestEmail Guest email
     * @param pageable Pagination information
     * @return Page of booking DTOs
     */
    Page<BookingDto> getGuestBookingHistory(String guestEmail, Pageable pageable);
    
    /**
     * Get bookings for check-in on specific date
     * @param date Check-in date
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsForCheckIn(LocalDate date);
    
    /**
     * Get bookings for check-out on specific date
     * @param date Check-out date
     * @return List of booking DTOs
     */
    List<BookingDto> getBookingsForCheckOut(LocalDate date);
    
    /**
     * Get active bookings (currently occupied)
     * @return List of booking DTOs
     */
    List<BookingDto> getActiveBookings();
    
    /**
     * Get overdue bookings (no-shows)
     * @return List of booking DTOs
     */
    List<BookingDto> getOverdueBookings();
    
    /**
     * Get bookings within a date range
     */
    List<BookingDto> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get upcoming check-ins for a specific date
     */
    List<BookingDto> getUpcomingCheckIns(LocalDate date);
    
    /**
     * Get upcoming check-outs for a specific date
     */
    List<BookingDto> getUpcomingCheckOuts(LocalDate date);
    
    /**
     * Update booking status
     */
    BookingDto updateBookingStatus(Long bookingId, BookingStatus status);
    
    /**
     * Check-in simplified method
     */
    BookingDto checkIn(Long bookingId);
    
    /**
     * Check-out simplified method
     */
    BookingDto checkOut(Long bookingId);
    
    /**
     * Search bookings with multiple filters (updated signature)
     */
    Page<BookingDto> searchBookings(String guestEmail, Long roomId, BookingStatus status,
                                   LocalDate checkInFrom, LocalDate checkInTo, 
                                   LocalDate checkOutFrom, LocalDate checkOutTo,
                                   Pageable pageable);
    
    /**
     * Check in a guest
     * @param bookingId Booking ID
     * @return Updated booking DTO
     */
    BookingDto checkInGuest(Long bookingId);
    
    /**
     * Check out a guest
     * @param bookingId Booking ID
     * @return Updated booking DTO
     */
    BookingDto checkOutGuest(Long bookingId);
    
    /**
     * Cancel a booking
     * @param bookingId Booking ID
     * @param cancellationReason Reason for cancellation
     * @return Updated booking DTO
     */
    BookingDto cancelBooking(Long bookingId, String cancellationReason);
    
    /**
     * Confirm a booking
     * @param bookingId Booking ID
     * @return Updated booking DTO
     */
    BookingDto confirmBooking(Long bookingId);
    
    /**
     * Mark booking as no-show
     * @param bookingId Booking ID
     * @return Updated booking DTO
     */
    BookingDto markAsNoShow(Long bookingId);
    
    /**
     * Search bookings with filters
     * @param guestEmail Guest email (optional)
     * @param guestName Guest name (optional)
     * @param status Booking status (optional)
     * @param roomNumber Room number (optional)
     * @param checkInDate Check-in date (optional)
     * @param checkOutDate Check-out date (optional)
     * @param pageable Pagination information
     * @return Page of booking DTOs
     */
    Page<BookingDto> searchBookings(String guestEmail, String guestName, BookingStatus status,
                                   String roomNumber, LocalDate checkInDate, LocalDate checkOutDate,
                                   Pageable pageable);
    
    /**
     * Check if room is available for booking dates
     * @param roomId Room ID
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return true if available, false otherwise
     */
    boolean isRoomAvailableForBooking(Long roomId, LocalDate checkIn, LocalDate checkOut);
    
    /**
     * Get booking statistics by status for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Map of status to count
     */
    Map<BookingStatus, Long> getBookingStatsByStatus(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get total revenue for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get paid revenue for a period
     * @param startDate Start date
     * @param endDate End date
     * @return Paid revenue
     */
    Double getPaidRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Delete a booking
     * @param bookingId Booking ID
     */
    void deleteBooking(Long bookingId);
}
