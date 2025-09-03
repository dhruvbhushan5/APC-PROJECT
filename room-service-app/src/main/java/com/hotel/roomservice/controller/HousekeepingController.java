package com.hotel.roomservice.controller;

import com.hotel.roomservice.entity.HousekeepingRequest;
import com.hotel.roomservice.repository.HousekeepingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/housekeeping")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HousekeepingController {
    
    private final HousekeepingRequestRepository housekeepingRequestRepository;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHousekeepingRequest(@RequestBody CreateHousekeepingRequest request) {
        log.info("Creating housekeeping request for room: {}", request.getRoomNumber());
        
        try {
            HousekeepingRequest housekeepingRequest = HousekeepingRequest.builder()
                    .bookingId(request.getBookingId())
                    .roomNumber(request.getRoomNumber())
                    .guestName(request.getGuestName())
                    .guestPhone(request.getGuestPhone())
                    .requestType(request.getRequestType())
                    .description(request.getDescription())
                    .priority(request.getPriority())
                    .status(HousekeepingRequest.RequestStatus.PENDING)
                    .requestedTime(request.getPreferredTime())
                    .build();
            
            HousekeepingRequest savedRequest = housekeepingRequestRepository.save(housekeepingRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedRequest);
            response.put("message", "Housekeeping request created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating housekeeping request: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create housekeeping request: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRequests() {
        log.info("Fetching all housekeeping requests");
        
        try {
            List<HousekeepingRequest> requests = housekeepingRequestRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);
            response.put("message", "Requests retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching requests: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch requests: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRequest(@PathVariable Long id) {
        log.info("Fetching housekeeping request with ID: {}", id);
        
        try {
            HousekeepingRequest request = housekeepingRequestRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", request);
            response.put("message", "Request retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching request {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch request: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/room/{roomNumber}")
    public ResponseEntity<Map<String, Object>> getRequestsByRoom(@PathVariable String roomNumber) {
        log.info("Fetching housekeeping requests for room: {}", roomNumber);
        
        try {
            List<HousekeepingRequest> requests = housekeepingRequestRepository.findByRoomNumber(roomNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);
            response.put("message", "Room requests retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching requests for room {}: {}", roomNumber, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch room requests: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getRequestsByStatus(@PathVariable HousekeepingRequest.RequestStatus status) {
        log.info("Fetching housekeeping requests with status: {}", status);
        
        try {
            List<HousekeepingRequest> requests = housekeepingRequestRepository.findByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);
            response.put("message", "Status requests retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching requests with status {}: {}", status, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch status requests: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getRequestsByType(@PathVariable HousekeepingRequest.RequestType type) {
        log.info("Fetching housekeeping requests with type: {}", type);
        
        try {
            List<HousekeepingRequest> requests = housekeepingRequestRepository.findByRequestType(type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);
            response.put("message", "Type requests retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching requests with type {}: {}", type, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch type requests: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateRequestStatus(@PathVariable Long id, @RequestBody UpdateRequestStatusRequest request) {
        log.info("Updating request {} status to: {}", id, request.getStatus());
        
        try {
            HousekeepingRequest housekeepingRequest = housekeepingRequestRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            
            housekeepingRequest.setStatus(request.getStatus());
            
            if (request.getAssignedStaff() != null) {
                housekeepingRequest.setAssignedStaff(request.getAssignedStaff());
            }
            
            if (request.getStaffNotes() != null) {
                housekeepingRequest.setCompletionNotes(request.getStaffNotes());
            }
            
            // Set completion time if completed
            if (request.getStatus() == HousekeepingRequest.RequestStatus.COMPLETED) {
                housekeepingRequest.setCompletedTime(LocalDateTime.now());
            }
            
            HousekeepingRequest updatedRequest = housekeepingRequestRepository.save(housekeepingRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedRequest);
            response.put("message", "Request status updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating request status: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update request status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PatchMapping("/{id}/assign")
    public ResponseEntity<Map<String, Object>> assignStaff(@PathVariable Long id, @RequestBody AssignStaffRequest request) {
        log.info("Assigning staff {} to request {}", request.getStaffName(), id);
        
        try {
            HousekeepingRequest housekeepingRequest = housekeepingRequestRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            
            housekeepingRequest.setAssignedStaff(request.getStaffName());
            housekeepingRequest.setStatus(HousekeepingRequest.RequestStatus.IN_PROGRESS);
            
            HousekeepingRequest updatedRequest = housekeepingRequestRepository.save(housekeepingRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedRequest);
            response.put("message", "Staff assigned successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning staff: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to assign staff: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Request DTOs
    public static class CreateHousekeepingRequest {
        private Long bookingId;
        private String roomNumber;
        private String guestName;
        private String guestPhone;
        private HousekeepingRequest.RequestType requestType;
        private String description;
        private HousekeepingRequest.Priority priority;
        private LocalDateTime preferredTime;
        
        // Getters and setters
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        
        public String getGuestName() { return guestName; }
        public void setGuestName(String guestName) { this.guestName = guestName; }
        
        public String getGuestPhone() { return guestPhone; }
        public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
        
        public HousekeepingRequest.RequestType getRequestType() { return requestType; }
        public void setRequestType(HousekeepingRequest.RequestType requestType) { this.requestType = requestType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public HousekeepingRequest.Priority getPriority() { return priority; }
        public void setPriority(HousekeepingRequest.Priority priority) { this.priority = priority; }
        
        public LocalDateTime getPreferredTime() { return preferredTime; }
        public void setPreferredTime(LocalDateTime preferredTime) { this.preferredTime = preferredTime; }
    }
    
    public static class UpdateRequestStatusRequest {
        private HousekeepingRequest.RequestStatus status;
        private String assignedStaff;
        private String staffNotes;
        
        // Getters and setters
        public HousekeepingRequest.RequestStatus getStatus() { return status; }
        public void setStatus(HousekeepingRequest.RequestStatus status) { this.status = status; }
        
        public String getAssignedStaff() { return assignedStaff; }
        public void setAssignedStaff(String assignedStaff) { this.assignedStaff = assignedStaff; }
        
        public String getStaffNotes() { return staffNotes; }
        public void setStaffNotes(String staffNotes) { this.staffNotes = staffNotes; }
    }
    
    public static class AssignStaffRequest {
        private String staffName;
        
        // Getters and setters
        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }
    }
}
