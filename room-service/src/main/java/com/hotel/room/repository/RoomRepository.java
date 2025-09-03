package com.hotel.room.repository;

import com.hotel.room.entity.Room;
import com.hotel.common.dto.RoomType;
import com.hotel.common.dto.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Basic finder methods
    Optional<Room> findByRoomNumber(String roomNumber);
    
    List<Room> findByStatus(RoomStatus status);
    
    List<Room> findByRoomType(RoomType roomType);
    
    List<Room> findByFloorNumber(Integer floorNumber);
    
    // Advanced query methods
    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.roomType = :roomType")
    List<Room> findByStatusAndRoomType(@Param("status") RoomStatus status, 
                                      @Param("roomType") RoomType roomType);
    
    @Query("SELECT r FROM Room r WHERE r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<Room> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                               @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT r FROM Room r WHERE r.capacity >= :minCapacity")
    List<Room> findByMinimumCapacity(@Param("minCapacity") Integer minCapacity);
    
    // Available rooms for specific dates (excluding booked rooms)
    @Query("""
        SELECT r FROM Room r 
        WHERE r.status = 'AVAILABLE' 
        AND r.id NOT IN (
            SELECT b.room.id FROM Booking b 
            WHERE b.status IN ('CONFIRMED', 'CHECKED_IN') 
            AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)
        )
        """)
    List<Room> findAvailableRoomsForDates(@Param("checkIn") LocalDate checkIn, 
                                         @Param("checkOut") LocalDate checkOut);
    
    // Available rooms by type for specific dates
    @Query("""
        SELECT r FROM Room r 
        WHERE r.status = 'AVAILABLE' 
        AND r.roomType = :roomType
        AND r.id NOT IN (
            SELECT b.room.id FROM Booking b 
            WHERE b.status IN ('CONFIRMED', 'CHECKED_IN') 
            AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)
        )
        """)
    List<Room> findAvailableRoomsByTypeForDates(@Param("roomType") RoomType roomType,
                                               @Param("checkIn") LocalDate checkIn, 
                                               @Param("checkOut") LocalDate checkOut);
    
    // Search rooms with filters
    @Query("""
        SELECT r FROM Room r 
        WHERE (:roomType IS NULL OR r.roomType = :roomType)
        AND (:minPrice IS NULL OR r.pricePerNight >= :minPrice)
        AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice)
        AND (:minCapacity IS NULL OR r.capacity >= :minCapacity)
        AND (:petFriendly IS NULL OR r.petFriendly = :petFriendly)
        AND (:smokingAllowed IS NULL OR r.smokingAllowed = :smokingAllowed)
        AND (:view IS NULL OR LOWER(r.view) LIKE LOWER(CONCAT('%', :view, '%')))
        """)
    Page<Room> findRoomsWithFilters(@Param("roomType") RoomType roomType,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minCapacity") Integer minCapacity,
                                   @Param("petFriendly") Boolean petFriendly,
                                   @Param("smokingAllowed") Boolean smokingAllowed,
                                   @Param("view") String view,
                                   Pageable pageable);
    
    // Count available rooms by type
    @Query("""
        SELECT r.roomType, COUNT(r) FROM Room r 
        WHERE r.status = 'AVAILABLE'
        GROUP BY r.roomType
        """)
    List<Object[]> countAvailableRoomsByType();
    
    // Rooms by amenities (contains search)
    @Query("SELECT r FROM Room r WHERE LOWER(r.amenities) LIKE LOWER(CONCAT('%', :amenity, '%'))")
    List<Room> findByAmenityContaining(@Param("amenity") String amenity);
    
    // Update room status
    @Query("UPDATE Room r SET r.status = :status WHERE r.id = :roomId")
    int updateRoomStatus(@Param("roomId") Long roomId, @Param("status") RoomStatus status);
    
    // Statistics queries
    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    Long countByStatus(@Param("status") RoomStatus status);
    
    @Query("SELECT AVG(r.pricePerNight) FROM Room r WHERE r.roomType = :roomType")
    BigDecimal getAveragePriceByRoomType(@Param("roomType") RoomType roomType);
}
