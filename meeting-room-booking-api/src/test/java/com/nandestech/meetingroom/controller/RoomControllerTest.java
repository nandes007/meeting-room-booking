package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.service.RoomService;
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

import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private JsonMapper jsonMapper;

    private static final String TOKEN = "valid-token";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        when(pasetoTokenService.verifyToken(TOKEN)).thenReturn("user");
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

        when(roomService.createRoom(any(RoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Room 1"));
    }

    @Test
    void createRoom_ValidationError() throws Exception {
        RoomRequest request = new RoomRequest("", null, "", null); // Invalid fields

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_ServiceError() throws Exception {
        RoomRequest request = new RoomRequest("Room 1", 10, "Location 1", true);
        when(roomService.createRoom(any(RoomRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("failed to create room"));
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
                .andExpect(jsonPath("$.message").value("failed to get rooms"));
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
                .andExpect(jsonPath("$.message").value("failed to get room"));
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

        when(roomService.updateRoom(eq(1L), any(RoomRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Updated Room"));
    }

    @Test
    void updateRoom_ServiceError() throws Exception {
        RoomRequest request = new RoomRequest("Updated Room", 20, "Location 2", false);
        when(roomService.updateRoom(eq(1L), any(RoomRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(patch("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("failed to update room"));
    }

    @Test
    void deleteRoom_Success() throws Exception {
        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("room deleted successfully"));
    }

    @Test
    void deleteRoom_ServiceError() throws Exception {
        doThrow(new RuntimeException("Error")).when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/v1/rooms/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("failed to delete room"));
    }
}
