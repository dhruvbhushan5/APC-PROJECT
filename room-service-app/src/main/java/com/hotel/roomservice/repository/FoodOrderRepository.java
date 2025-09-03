package com.hotel.roomservice.repository;

import com.hotel.roomservice.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    
    List<FoodOrder> findByRoomNumber(String roomNumber);
    
    List<FoodOrder> findByBookingId(Long bookingId);
    
    List<FoodOrder> findByStatus(FoodOrder.OrderStatus status);
    
    List<FoodOrder> findByStatusIn(List<FoodOrder.OrderStatus> statuses);
    
    @Query("SELECT o FROM FoodOrder o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate ORDER BY o.orderDate DESC")
    List<FoodOrder> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM FoodOrder o WHERE o.roomNumber = :roomNumber AND o.status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY o.orderDate DESC")
    List<FoodOrder> findActiveOrdersByRoom(@Param("roomNumber") String roomNumber);
    
    @Query("SELECT COUNT(o) FROM FoodOrder o WHERE o.status = :status")
    Long countByStatus(@Param("status") FoodOrder.OrderStatus status);
}
