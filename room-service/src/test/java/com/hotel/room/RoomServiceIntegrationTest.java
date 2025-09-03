package com.hotel.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.dto.RoomDto;
import com.hotel.common.dto.RoomType;
import com.hotel.common.dto.RoomStatus;
import com.hotel.room.config.TestContainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
class RoomServiceIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoom_ShouldCreateAndReturnRoom() throws Exception {
        // Given
        RoomDto roomDto = RoomDto.builder()
                .roomNumber("501")
                .roomType(RoomType.DELUXE)
                .pricePerNight(BigDecimal.valueOf(250.00))
                .status(RoomStatus.AVAILABLE)
                .description("Deluxe room with ocean view")
                .capacity(2)
                .build();

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomNumber").value("501"))
                .andExpect(jsonPath("$.data.roomType").value("DELUXE"))
                .andExpect(jsonPath("$.data.pricePerNight").value(250.00))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
    }

    @Test
    @WithMockUser
    void getAllRooms_ShouldReturnPagedResults() throws Exception {
        // Given - Create a room first
        createTestRoom("301", RoomType.STANDARD, BigDecimal.valueOf(150.00));

        // When & Then
        mockMvc.perform(get("/api/rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].roomNumber").value("301"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoom_ShouldUpdateAndReturnRoom() throws Exception {
        // Given - Create a room first
        Long roomId = createTestRoom("401", RoomType.SUITE, BigDecimal.valueOf(400.00));

        RoomDto updateDto = RoomDto.builder()
                .roomNumber("401")
                .roomType(RoomType.PRESIDENTIAL)
                .pricePerNight(BigDecimal.valueOf(500.00))
                .status(RoomStatus.AVAILABLE)
                .description("Updated presidential suite")
                .capacity(4)
                .build();

        // When & Then
        mockMvc.perform(put("/api/rooms/" + roomId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomType").value("PRESIDENTIAL"))
                .andExpect(jsonPath("$.data.pricePerNight").value(500.00));
    }

    @Test
    @WithMockUser
    void getRoomById_ShouldReturnRoom_WhenExists() throws Exception {
        // Given
        Long roomId = createTestRoom("201", RoomType.DOUBLE, BigDecimal.valueOf(200.00));

        // When & Then
        mockMvc.perform(get("/api/rooms/" + roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(roomId))
                .andExpect(jsonPath("$.data.roomNumber").value("201"))
                .andExpect(jsonPath("$.data.roomType").value("DOUBLE"));
    }

    @Test
    @WithMockUser
    void getRoomById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rooms/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoom_ShouldDeleteRoom_WhenExists() throws Exception {
        // Given
        Long roomId = createTestRoom("101", RoomType.SINGLE, BigDecimal.valueOf(100.00));

        // When & Then
        mockMvc.perform(delete("/api/rooms/" + roomId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify room is deleted
        mockMvc.perform(get("/api/rooms/" + roomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchRoomsByType_ShouldReturnFilteredRooms() throws Exception {
        // Given
        createTestRoom("111", RoomType.SINGLE, BigDecimal.valueOf(100.00));
        createTestRoom("112", RoomType.DOUBLE, BigDecimal.valueOf(150.00));
        createTestRoom("113", RoomType.SINGLE, BigDecimal.valueOf(110.00));

        // When & Then
        mockMvc.perform(get("/api/rooms/type/SINGLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].roomType").value("SINGLE"))
                .andExpect(jsonPath("$.data[1].roomType").value("SINGLE"));
    }

    @Test
    @WithMockUser
    void searchRoomsByPriceRange_ShouldReturnFilteredRooms() throws Exception {
        // Given
        createTestRoom("121", RoomType.SINGLE, BigDecimal.valueOf(80.00));
        createTestRoom("122", RoomType.DOUBLE, BigDecimal.valueOf(150.00));
        createTestRoom("123", RoomType.SUITE, BigDecimal.valueOf(300.00));

        // When & Then
        mockMvc.perform(get("/api/rooms/search")
                        .param("minPrice", "100")
                        .param("maxPrice", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].pricePerNight").value(150.00));
    }

    @Test
    @WithMockUser
    void getAvailableRooms_ShouldReturnOnlyAvailableRooms() throws Exception {
        // Given
        createTestRoom("131", RoomType.SINGLE, BigDecimal.valueOf(100.00), RoomStatus.AVAILABLE);
        createTestRoom("132", RoomType.DOUBLE, BigDecimal.valueOf(150.00), RoomStatus.OCCUPIED);
        createTestRoom("133", RoomType.SUITE, BigDecimal.valueOf(250.00), RoomStatus.AVAILABLE);

        // When & Then
        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data[1].status").value("AVAILABLE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRoom_ShouldReturnForbidden_WhenInsufficientRole() throws Exception {
        // Given
        RoomDto roomDto = RoomDto.builder()
                .roomNumber("901")
                .roomType(RoomType.STANDARD)
                .pricePerNight(BigDecimal.valueOf(100.00))
                .status(RoomStatus.AVAILABLE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoom_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given - Invalid room with missing required fields
        RoomDto invalidRoom = RoomDto.builder()
                .roomNumber("") // Invalid empty room number
                .roomType(null) // Missing room type
                .pricePerNight(BigDecimal.valueOf(-100.00)) // Invalid negative price
                .build();

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoom)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Helper method to create a test room
     */
    private Long createTestRoom(String roomNumber, RoomType roomType, BigDecimal price) throws Exception {
        return createTestRoom(roomNumber, roomType, price, RoomStatus.AVAILABLE);
    }

    /**
     * Helper method to create a test room with specific status
     */
    private Long createTestRoom(String roomNumber, RoomType roomType, BigDecimal price, RoomStatus status) throws Exception {
        RoomDto roomDto = RoomDto.builder()
                .roomNumber(roomNumber)
                .roomType(roomType)
                .pricePerNight(price)
                .status(status)
                .description("Test room")
                .capacity(2)
                .build();

        String response = mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the room ID from the response
        return objectMapper.readTree(response).path("data").path("id").asLong();
    }
}
