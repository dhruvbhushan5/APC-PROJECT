package com.hotel.room.repository;

import com.hotel.room.entity.Booking;
import com.hotel.room.entity.Room;
import com.hotel.common.dto.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Basic finder methods
    List<Booking> findByGuestEmail(String guestEmail);
    
    List<Booking> findByGuestId(Long guestId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByRoom(Room room);
    
    Optional<Booking> findByIdAndGuestEmail(Long bookingId, String guestEmail);
    
    // Date-based queries
    @Query("SELECT b FROM Booking b WHERE b.checkInDate = :date")
    List<Booking> findBookingsForCheckInDate(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.checkOutDate = :date")
    List<Booking> findBookingsForCheckOutDate(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.checkInDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    // Active bookings (currently occupied)
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.status = 'CHECKED_IN' 
        OR (b.status = 'CONFIRMED' AND b.checkInDate <= :currentDate AND b.checkOutDate > :currentDate)
        """)
    List<Booking> findActiveBookings(@Param("currentDate") LocalDate currentDate);
    
    // Check room availability for dates
    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN false ELSE true END 
        FROM Booking b 
        WHERE b.room.id = :roomId 
        AND b.status IN ('CONFIRMED', 'CHECKED_IN') 
        AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)
        """)
    Boolean isRoomAvailableForDates(@Param("roomId") Long roomId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);
    
    // Find conflicting bookings
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.room.id = :roomId 
        AND b.status IN ('CONFIRMED', 'CHECKED_IN') 
        AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)
        """)
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut);
    
    // Guest booking history
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.guestEmail = :guestEmail 
        ORDER BY b.createdAt DESC
        """)
    Page<Booking> findGuestBookingHistory(@Param("guestEmail") String guestEmail, 
                                         Pageable pageable);
    
    // Bookings requiring check-in today
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.status = 'CONFIRMED' 
        AND b.checkInDate = :today
        """)
    List<Booking> findBookingsForCheckInToday(@Param("today") LocalDate today);
    
    // Bookings requiring check-out today
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.status = 'CHECKED_IN' 
        AND b.checkOutDate = :today
        """)
    List<Booking> findBookingsForCheckOutToday(@Param("today") LocalDate today);
    
    // Overdue bookings (no-shows)
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.status = 'CONFIRMED' 
        AND b.checkInDate < :currentDate
        """)
    List<Booking> findOverdueBookings(@Param("currentDate") LocalDate currentDate);
    
    // Revenue queries
    @Query("""
        SELECT SUM(b.totalAmount) FROM Booking b 
        WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') 
        AND b.createdAt BETWEEN :startDate AND :endDate
        """)
    Double getTotalRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("""
        SELECT SUM(b.paidAmount) FROM Booking b 
        WHERE b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') 
        AND b.createdAt BETWEEN :startDate AND :endDate
        """)
    Double getPaidRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
    
    // Statistics
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);
    
    @Query("""
        SELECT b.status, COUNT(b) FROM Booking b 
        WHERE b.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY b.status
        """)
    List<Object[]> getBookingStatsByStatus(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    // Update booking status
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :bookingId")
    int updateBookingStatus(@Param("bookingId") Long bookingId, 
                           @Param("status") BookingStatus status);
    
    // Search bookings with filters
    @Query("""
        SELECT b FROM Booking b 
        WHERE (:guestEmail IS NULL OR LOWER(b.guestEmail) LIKE LOWER(CONCAT('%', :guestEmail, '%')))
        AND (:guestName IS NULL OR LOWER(b.guestName) LIKE LOWER(CONCAT('%', :guestName, '%')))
        AND (:status IS NULL OR b.status = :status)
        AND (:roomNumber IS NULL OR b.room.roomNumber = :roomNumber)
        AND (:checkInDate IS NULL OR b.checkInDate >= :checkInDate)
        AND (:checkOutDate IS NULL OR b.checkOutDate <= :checkOutDate)
        ORDER BY b.createdAt DESC
        """)
    Page<Booking> searchBookings(@Param("guestEmail") String guestEmail,
                                @Param("guestName") String guestName,
                                @Param("status") BookingStatus status,
                                @Param("roomNumber") String roomNumber,
                                @Param("checkInDate") LocalDate checkInDate,
                                @Param("checkOutDate") LocalDate checkOutDate,
                                Pageable pageable);
}
