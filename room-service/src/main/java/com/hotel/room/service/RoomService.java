package com.hotel.room.service;

import com.hotel.common.dto.RoomDto;
import com.hotel.common.dto.RoomStatus;
import com.hotel.common.dto.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service interface for room management operations
 */
public interface RoomService {
    
    /**
     * Create a new room
     */
    RoomDto createRoom(RoomDto roomDto);
    
    /**
     * Update an existing room
     */
    RoomDto updateRoom(Long roomId, RoomDto roomDto);
    
    /**
     * Get room by ID
     */
    RoomDto getRoomById(Long roomId);
    
    /**
     * Get all rooms with pagination
     */
    Page<RoomDto> getAllRooms(Pageable pageable);
    
    /**
     * Get rooms by type
     */
    Page<RoomDto> getRoomsByType(RoomType roomType, Pageable pageable);
    
    /**
     * Get rooms by status
     */
    Page<RoomDto> getRoomsByStatus(RoomStatus status, Pageable pageable);
    
    /**
     * Search rooms with criteria
     */
    Page<RoomDto> searchRooms(RoomType roomType, RoomStatus status, 
                              BigDecimal minPrice, BigDecimal maxPrice,
                              Integer minCapacity, Integer maxCapacity,
                              LocalDate checkInDate, LocalDate checkOutDate,
                              Pageable pageable);
    
    /**
     * Get available rooms for date range
     */
    Page<RoomDto> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Pageable pageable);
    
    /**
     * Update room status
     */
    RoomDto updateRoomStatus(Long roomId, RoomStatus status);
    
    /**
     * Delete room
     */
    void deleteRoom(Long roomId);
    
    /**
     * Check if room is available for booking
     */
    boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
    
    /**
     * Get room statistics by type
     */
    Page<RoomDto> getRoomStatistics(Pageable pageable);
    
    /**
     * Get room maintenance history
     */
    Page<RoomDto> getRoomMaintenanceHistory(Long roomId, Pageable pageable);
}
