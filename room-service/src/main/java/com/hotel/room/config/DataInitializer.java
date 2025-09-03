package com.hotel.room.config;

import com.hotel.common.dto.RoomStatus;
import com.hotel.common.dto.RoomType;
import com.hotel.room.entity.Room;
import com.hotel.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("h2")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoomRepository roomRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            log.info("Initializing room data for H2 database...");
            createSampleRooms();
            log.info("Sample room data created successfully");
        }
    }
    
    private void createSampleRooms() {
        // Create sample rooms
        Room[] rooms = {
            Room.builder()
                .roomNumber("101")
                .roomType(RoomType.SINGLE)
                .floorNumber(1)
                .pricePerNight(new BigDecimal("99.99"))
                .status(RoomStatus.AVAILABLE)
                .description("Cozy single room with city view")
                .capacity(1)
                .amenities("WiFi, AC, TV")
                .area(new BigDecimal("25.0"))
                .petFriendly(false)
                .smokingAllowed(false)
                .view("City")
                .build(),
                
            Room.builder()
                .roomNumber("102")
                .roomType(RoomType.DOUBLE)
                .floorNumber(1)
                .pricePerNight(new BigDecimal("149.99"))
                .status(RoomStatus.AVAILABLE)
                .description("Comfortable double room with garden view")
                .capacity(2)
                .amenities("WiFi, AC, TV, Minibar")
                .area(new BigDecimal("35.0"))
                .petFriendly(false)
                .smokingAllowed(false)
                .view("Garden")
                .build(),
                
            Room.builder()
                .roomNumber("201")
                .roomType(RoomType.SUITE)
                .floorNumber(2)
                .pricePerNight(new BigDecimal("299.99"))
                .status(RoomStatus.AVAILABLE)
                .description("Luxury suite with ocean view and balcony")
                .capacity(4)
                .amenities("WiFi, AC, TV, Minibar, Balcony, Living Area")
                .area(new BigDecimal("75.0"))
                .petFriendly(true)
                .smokingAllowed(false)
                .view("Ocean")
                .build(),
                
            Room.builder()
                .roomNumber("202")
                .roomType(RoomType.DELUXE)
                .floorNumber(2)
                .pricePerNight(new BigDecimal("199.99"))
                .status(RoomStatus.AVAILABLE)
                .description("Deluxe room with premium amenities")
                .capacity(2)
                .amenities("WiFi, AC, TV, Minibar, Premium Bathroom")
                .area(new BigDecimal("45.0"))
                .petFriendly(false)
                .smokingAllowed(false)
                .view("City")
                .build(),
                
            Room.builder()
                .roomNumber("301")
                .roomType(RoomType.SINGLE)
                .floorNumber(3)
                .pricePerNight(new BigDecimal("89.99"))
                .status(RoomStatus.AVAILABLE)
                .description("Budget-friendly single room")
                .capacity(1)
                .amenities("WiFi, AC, TV")
                .area(new BigDecimal("20.0"))
                .petFriendly(false)
                .smokingAllowed(false)
                .view("Courtyard")
                .build(),
                
            Room.builder()
                .roomNumber("302")
                .roomType(RoomType.DOUBLE)
                .floorNumber(3)
                .pricePerNight(new BigDecimal("159.99"))
                .status(RoomStatus.OCCUPIED)
                .description("Premium double room with mountain view")
                .capacity(2)
                .amenities("WiFi, AC, TV, Minibar, Mountain View")
                .area(new BigDecimal("40.0"))
                .petFriendly(true)
                .smokingAllowed(false)
                .view("Mountain")
                .build()
        };
        
        for (Room room : rooms) {
            roomRepository.save(room);
        }
    }
}
