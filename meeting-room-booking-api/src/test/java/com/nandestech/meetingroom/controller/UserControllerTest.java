package com.nandestech.meetingroom.controller;

import tools.jackson.databind.ObjectMapper;
import com.nandestech.meetingroom.dto.UserRequest;
import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.UserRepository;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ADMIN_TOKEN = "admin-token";
    private static final String ADMIN_USERNAME = "superadmin";
    private static final String USER_TOKEN = "user-token";
    private static final String USER_USERNAME = "employee";

    @BeforeEach
    void setUp() {
        // Mock SUPERADMIN
        when(pasetoTokenService.verifyToken(ADMIN_TOKEN)).thenReturn(ADMIN_USERNAME);
        User admin = new User();
        admin.setId(1L);
        admin.setUsername(ADMIN_USERNAME);
        admin.setRole("SUPERADMIN");
        when(userRepository.findByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(admin));

        // Mock EMPLOYEE
        when(pasetoTokenService.verifyToken(USER_TOKEN)).thenReturn(USER_USERNAME);
        User employee = new User();
        employee.setId(2L);
        employee.setUsername(USER_USERNAME);
        employee.setRole("EMPLOYEE");
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(employee));
    }

    @Test
    void getCurrentUser_Success() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username(ADMIN_USERNAME)
                .role("SUPERADMIN")
                .build();

        when(userService.getCurrentUser(ADMIN_USERNAME)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/current")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value(ADMIN_USERNAME));
    }

    @Test
    void getAllUsers_Success_AsSuperAdmin() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username(ADMIN_USERNAME)
                .build();
        Page<UserResponse> userPage = new PageImpl<>(Collections.singletonList(userResponse));
        
        when(userService.getAllUsers(1, 10)).thenReturn(userPage);

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content[0].username").value(ADMIN_USERNAME))
                .andExpect(jsonPath("$.data.page.totalElements").value(1));
    }

    @Test
    void getAllUsers_Forbidden_AsEmployee() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + USER_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("unauthorized"));
    }

    @Test
    void createUser_Success_AsSuperAdmin() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("New User")
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .build();
        UserResponse response = UserResponse.builder().id(3L).username("newuser").build();

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new RuntimeException("failed to get user"));

        mockMvc.perform(get("/api/v1/users/99")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("failed to get user"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserRequest request = UserRequest.builder().name("Updated").build();
        UserResponse response = UserResponse.builder().id(2L).name("Updated").build();

        when(userService.updateUser(eq(2L), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/2")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Updated"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(2L);

        mockMvc.perform(delete("/api/v1/users/2")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("user deleted successfully"));
    }

    @Test
    void unauthorized_Access_NoToken() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("unauthorized"));
    }
}
