package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.UserRepository;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    private static final String TOKEN = "valid-token";

    @BeforeEach
    void setUp() {
        when(pasetoTokenService.verifyToken(TOKEN)).thenReturn("user");
        User user = new User();
        user.setUsername("user");
        user.setRole("ADMIN");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    }

    @Test
    void unauthorized_MissingToken() throws Exception {
        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("unauthorized"));
    }

    @Test
    void createRoom_Success() throws Exception {
        RoomRequest request = new RoomRequest("Room 1", 10, "Location 1", true);
        RoomResponse response = RoomResponse.builder()
                .name("Room 1")
                .capacity(10)
                .location("Location 1")
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(roomService.createRoom(any(RoomRequest.class), eq("ADMIN"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Room 1"));
    }

    @Test
    void createRoom_Forbidden() throws Exception {
        RoomRequest request = new RoomRequest("Room 1", 10, "Location 1", true);
        
        // Mock USER role for this request
        User user = new User();
        user.setUsername("user");
        user.setRole("USER");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        when(roomService.createRoom(any(RoomRequest.class), eq("USER")))
                .thenThrow(new RuntimeException("Access denied: Only administrators can perform this action"));

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Access denied: Only administrators can perform this action"));
    }

    @Test
    void createRoom_ValidationError() throws Exception {
        RoomRequest request = new RoomRequest("", null, "", null); // Invalid fields

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").value("Room name is required"));
    }

    @Test
    void createRoom_ServiceError() throws Exception {
        RoomRequest request = new RoomRequest("Room 1", 10, "Location 1", true);
        when(roomService.createRoom(any(RoomRequest.class), eq("ADMIN"))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Database error"));
    }

    @Test
    void getAllRooms_Success() throws Exception {
        RoomResponse response = RoomResponse.builder()
                .name("Room 1")
                .build();
        List<RoomResponse> data = Collections.singletonList(response);

        when(roomService.getAllRooms()).thenReturn(data);

        mockMvc.perform(get("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].name").value("Room 1"));
    }

    @Test
    void getAllRooms_ServiceError() throws Exception {
        when(roomService.getAllRooms()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    void getRoomById_Success() throws Exception {
        RoomResponse response = RoomResponse.builder()
                .name("Room 1")
                .build();

        when(roomService.getRoomById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Room 1"));
    }

    @Test
    void getRoomById_NotFound() throws Exception {
        when(roomService.getRoomById(1L)).thenThrow(new RuntimeException("Room not found"));

        mockMvc.perform(get("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Room not found"));
    }

    @Test
    void updateRoom_Success() throws Exception {
        RoomRequest request = new RoomRequest("Updated Room", 20, "Location 2", false);
        RoomResponse response = RoomResponse.builder()
                .name("Updated Room")
                .capacity(20)
                .location("Location 2")
                .isAvailable(false)
                .build();

        when(roomService.updateRoom(eq(1L), any(RoomRequest.class), eq("ADMIN"))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Updated Room"));
    }

    @Test
    void updateRoom_ServiceError() throws Exception {
        RoomRequest request = new RoomRequest("Updated Room", 20, "Location 2", false);
        when(roomService.updateRoom(eq(1L), any(RoomRequest.class), eq("ADMIN"))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(patch("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    void deleteRoom_Success() throws Exception {
        doNothing().when(roomService).deleteRoom(1L, "ADMIN");

        mockMvc.perform(delete("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("room deleted successfully"));
    }

    @Test
    void deleteRoom_ServiceError() throws Exception {
        doThrow(new RuntimeException("Error")).when(roomService).deleteRoom(1L, "ADMIN");

        mockMvc.perform(delete("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .requestAttr("X-Role", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Error"));
    }
}
