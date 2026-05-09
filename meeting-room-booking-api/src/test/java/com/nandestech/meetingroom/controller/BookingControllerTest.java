package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.BookingRequest;
import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.UserRepository;
import com.nandestech.meetingroom.service.BookingService;
import com.nandestech.meetingroom.service.PasetoTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
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

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    private static final String TOKEN = "valid-token";
    private static final String USERNAME = "testuser";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    @BeforeEach
    void setUp() {
        when(pasetoTokenService.verifyToken(TOKEN)).thenReturn(USERNAME);
        
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setRole(ADMIN_ROLE);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    void unauthorized_MissingToken() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("unauthorized"));
    }

    private void mockUserRole(String role) {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setRole(role);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    void createBooking_Success() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        BookingRequest request = new BookingRequest(1L, start, end, null, "Meeting Description");
        
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .roomId(1L)
                .userId(1L)
                .startTime(start)
                .endTime(end)
                .status("pending")
                .description("Meeting Description")
                .build();

        when(bookingService.createBooking(any(BookingRequest.class), eq(USERNAME))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.description").value("Meeting Description"));
    }

    @Test
    void createBooking_ValidationError() throws Exception {
        BookingRequest request = new BookingRequest(null, null, null, null, ""); // Missing fields

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value(containsString("required")));
    }

    @Test
    void createBooking_OverlapError() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        BookingRequest request = new BookingRequest(1L, start, end, null, "Meeting Description");

        when(bookingService.createBooking(any(BookingRequest.class), eq(USERNAME)))
                .thenThrow(new RuntimeException("The room is already booked for the selected time slot"));

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("The room is already booked for the selected time slot"));
    }

    @Test
    void getAllBookings_AdminSuccess() throws Exception {
        mockUserRole(ADMIN_ROLE);
        BookingResponse response = BookingResponse.builder().id(1L).description("Admin View").build();
        Page<BookingResponse> page = new PageImpl<>(Collections.singletonList(response));
        when(bookingService.getAllBookings(eq(ADMIN_ROLE), eq(USERNAME), eq(1), eq(10))).thenReturn(page);
    
        mockMvc.perform(get("/api/v1/bookings")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content[0].description").value("Admin View"));
    }

    @Test
    void getAllBookings_UserSuccess() throws Exception {
        mockUserRole(USER_ROLE);
        BookingResponse response = BookingResponse.builder().id(1L).description("User View").build();
        Page<BookingResponse> page = new PageImpl<>(Collections.singletonList(response));
        when(bookingService.getAllBookings(eq(USER_ROLE), eq(USERNAME), eq(1), eq(10))).thenReturn(page);
    
        mockMvc.perform(get("/api/v1/bookings")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content[0].description").value("User View"));
    }

    @Test
    void getBookingById_Success() throws Exception {
        BookingResponse response = BookingResponse.builder().id(1L).description("Details").build();
        when(bookingService.getBookingById(eq(1L), eq(ADMIN_ROLE), eq(USERNAME))).thenReturn(response);

        mockMvc.perform(get("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.description").value("Details"));
    }

    @Test
    void getBookingById_Forbidden() throws Exception {
        mockUserRole(USER_ROLE);
        when(bookingService.getBookingById(eq(1L), eq(USER_ROLE), eq(USERNAME)))
                .thenThrow(new RuntimeException("Access denied: You can only view your own bookings"));

        mockMvc.perform(get("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Access denied: You can only view your own bookings"));
    }

    @Test
    void updateBooking_AdminSuccess() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        BookingRequest request = new BookingRequest(1L, start, end, "confirmed", "Updated");
        BookingResponse response = BookingResponse.builder().id(1L).description("Updated").build();

        when(bookingService.updateBooking(eq(1L), any(BookingRequest.class), eq(USERNAME), eq(ADMIN_ROLE))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.description").value("Updated"));
    }

    @Test
    void updateBooking_UserForbidden() throws Exception {
        mockUserRole(USER_ROLE);
        BookingRequest request = new BookingRequest(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), null, "Updated");

        when(bookingService.updateBooking(eq(1L), any(BookingRequest.class), eq(USERNAME), eq(USER_ROLE)))
                .thenThrow(new RuntimeException("Access denied: Only administrators can edit bookings"));

        mockMvc.perform(patch("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Access denied: Only administrators can edit bookings"));
    }

    @Test
    void deleteBooking_Success() throws Exception {
        doNothing().when(bookingService).deleteBooking(eq(1L), eq(USERNAME), eq(ADMIN_ROLE));

        mockMvc.perform(delete("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Booking cancelled successfully"));
    }

    @Test
    void deleteBooking_Forbidden() throws Exception {
        mockUserRole(USER_ROLE);
        doThrow(new RuntimeException("Access denied: You can only cancel your own bookings"))
                .when(bookingService).deleteBooking(eq(1L), eq(USERNAME), eq(USER_ROLE));

        mockMvc.perform(delete("/api/v1/bookings/1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Access denied: You can only cancel your own bookings"));
    }
}
