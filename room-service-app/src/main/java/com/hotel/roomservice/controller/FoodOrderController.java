package com.hotel.roomservice.controller;

import com.hotel.roomservice.entity.FoodOrder;
import com.hotel.roomservice.entity.OrderItem;
import com.hotel.roomservice.entity.MenuItem;
import com.hotel.roomservice.repository.FoodOrderRepository;
import com.hotel.roomservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/food-orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FoodOrderController {
    
    private final FoodOrderRepository foodOrderRepository;
    private final MenuItemRepository menuItemRepository;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFoodOrder(@RequestBody CreateFoodOrderRequest request) {
        log.info("Creating food order for room: {}", request.getRoomNumber());
        
        try {
            // Calculate totals
            BigDecimal subtotal = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (CreateFoodOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemRequest.getMenuItemId()));
                
                BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                subtotal = subtotal.add(itemTotal);
                
                OrderItem orderItem = OrderItem.builder()
                        .menuItem(menuItem)
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(menuItem.getPrice())
                        .totalPrice(itemTotal)
                        .specialRequests(itemRequest.getSpecialRequests())
                        .build();
                
                orderItems.add(orderItem);
            }
            
            // Calculate additional charges
            BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.1)); // 10% tax
            BigDecimal deliveryFee = BigDecimal.valueOf(50.0); // Fixed delivery fee
            BigDecimal totalAmount = subtotal.add(taxAmount).add(deliveryFee);
            
            // Create order
            FoodOrder order = FoodOrder.builder()
                    .bookingId(request.getBookingId())
                    .roomNumber(request.getRoomNumber())
                    .guestName(request.getGuestName())
                    .guestPhone(request.getGuestPhone())
                    .subtotal(subtotal)
                    .taxAmount(taxAmount)
                    .deliveryFee(deliveryFee)
                    .totalAmount(totalAmount)
                    .status(FoodOrder.OrderStatus.PENDING)
                    .specialInstructions(request.getSpecialInstructions())
                    .requestedDeliveryTime(request.getRequestedDeliveryTime())
                    .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(45)) // Default 45 minutes
                    .build();
            
            // Set order reference in order items
            orderItems.forEach(item -> item.setOrder(order));
            order.setOrderItems(orderItems);
            
            FoodOrder savedOrder = foodOrderRepository.save(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedOrder);
            response.put("message", "Food order created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating food order: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create food order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        log.info("Fetching all food orders");
        
        try {
            List<FoodOrder> orders = foodOrderRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders);
            response.put("message", "Orders retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch orders: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long id) {
        log.info("Fetching food order with ID: {}", id);
        
        try {
            FoodOrder order = foodOrderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", order);
            response.put("message", "Order retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching order {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/room/{roomNumber}")
    public ResponseEntity<Map<String, Object>> getOrdersByRoom(@PathVariable String roomNumber) {
        log.info("Fetching orders for room: {}", roomNumber);
        
        try {
            List<FoodOrder> orders = foodOrderRepository.findByRoomNumber(roomNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders);
            response.put("message", "Room orders retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching orders for room {}: {}", roomNumber, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch room orders: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        log.info("Updating order {} status to: {}", id, request.getStatus());
        
        try {
            FoodOrder order = foodOrderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            order.setStatus(request.getStatus());
            
            // Set delivery time based on status
            if (request.getStatus() == FoodOrder.OrderStatus.DELIVERED) {
                order.setActualDeliveryTime(LocalDateTime.now());
            }
            
            if (request.getDeliveryStaff() != null) {
                order.setDeliveryStaff(request.getDeliveryStaff());
            }
            
            FoodOrder updatedOrder = foodOrderRepository.save(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedOrder);
            response.put("message", "Order status updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update order status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Request DTOs
    public static class CreateFoodOrderRequest {
        private Long bookingId;
        private String roomNumber;
        private String guestName;
        private String guestPhone;
        private List<OrderItemRequest> orderItems;
        private String specialInstructions;
        private LocalDateTime requestedDeliveryTime;
        
        // Getters and setters
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        
        public String getGuestName() { return guestName; }
        public void setGuestName(String guestName) { this.guestName = guestName; }
        
        public String getGuestPhone() { return guestPhone; }
        public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
        
        public List<OrderItemRequest> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemRequest> orderItems) { this.orderItems = orderItems; }
        
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
        
        public LocalDateTime getRequestedDeliveryTime() { return requestedDeliveryTime; }
        public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) { this.requestedDeliveryTime = requestedDeliveryTime; }
        
        public static class OrderItemRequest {
            private Long menuItemId;
            private Integer quantity;
            private String specialRequests;
            
            // Getters and setters
            public Long getMenuItemId() { return menuItemId; }
            public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
            
            public Integer getQuantity() { return quantity; }
            public void setQuantity(Integer quantity) { this.quantity = quantity; }
            
            public String getSpecialRequests() { return specialRequests; }
            public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
        }
    }
    
    public static class UpdateOrderStatusRequest {
        private FoodOrder.OrderStatus status;
        private String deliveryStaff;
        
        // Getters and setters
        public FoodOrder.OrderStatus getStatus() { return status; }
        public void setStatus(FoodOrder.OrderStatus status) { this.status = status; }
        
        public String getDeliveryStaff() { return deliveryStaff; }
        public void setDeliveryStaff(String deliveryStaff) { this.deliveryStaff = deliveryStaff; }
    }
}
