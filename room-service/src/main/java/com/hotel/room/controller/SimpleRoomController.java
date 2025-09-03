package com.hotel.room.controller;

import com.hotel.room.entity.Room;
import com.hotel.room.repository.RoomRepository;
import com.hotel.common.dto.RoomStatus;
import com.hotel.common.dto.RoomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SimpleRoomController {
    
    private final RoomRepository roomRepository;
    
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        log.info("Fetching all available rooms");
        List<Room> availableRooms = roomRepository.findByStatus(RoomStatus.AVAILABLE);
        return ResponseEntity.ok(availableRooms);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Room>> getAllRooms() {
        log.info("Fetching all rooms");
        List<Room> allRooms = roomRepository.findAll();
        return ResponseEntity.ok(allRooms);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        log.info("Fetching room with ID: {}", id);
        return roomRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/types/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByType(@PathVariable RoomType roomType) {
        log.info("Fetching rooms by type: {}", roomType);
        List<Room> rooms = roomRepository.findByRoomType(roomType);
        return ResponseEntity.ok(rooms);
    }
}
