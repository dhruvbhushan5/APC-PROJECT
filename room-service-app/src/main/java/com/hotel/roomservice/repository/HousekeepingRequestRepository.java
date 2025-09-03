package com.hotel.roomservice.repository;

import com.hotel.roomservice.entity.HousekeepingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HousekeepingRequestRepository extends JpaRepository<HousekeepingRequest, Long> {
    
    List<HousekeepingRequest> findByRoomNumber(String roomNumber);
    
    List<HousekeepingRequest> findByBookingId(Long bookingId);
    
    List<HousekeepingRequest> findByStatus(HousekeepingRequest.RequestStatus status);
    
    List<HousekeepingRequest> findByRequestType(HousekeepingRequest.RequestType requestType);
    
    List<HousekeepingRequest> findByPriority(HousekeepingRequest.Priority priority);
    
    List<HousekeepingRequest> findByAssignedStaff(String assignedStaff);
    
    @Query("SELECT h FROM HousekeepingRequest h WHERE h.status IN ('PENDING', 'IN_PROGRESS') ORDER BY h.priority DESC, h.requestedAt ASC")
    List<HousekeepingRequest> findActiveRequests();
    
    @Query("SELECT h FROM HousekeepingRequest h WHERE h.requestedAt >= :startDate AND h.requestedAt <= :endDate ORDER BY h.requestedAt DESC")
    List<HousekeepingRequest> findRequestsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(h) FROM HousekeepingRequest h WHERE h.status = :status")
    Long countByStatus(@Param("status") HousekeepingRequest.RequestStatus status);
    
    @Query("SELECT h FROM HousekeepingRequest h WHERE h.scheduledTime IS NOT NULL AND h.scheduledTime >= :startTime AND h.scheduledTime <= :endTime ORDER BY h.scheduledTime")
    List<HousekeepingRequest> findScheduledRequests(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
