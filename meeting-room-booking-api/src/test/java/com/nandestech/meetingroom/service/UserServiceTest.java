package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.UserRequest;
import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.entity.Booking;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.BookingRepository;
import com.nandestech.meetingroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole("EMPLOYEE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createUser_Success() {
        UserRequest request = UserRequest.builder()
                .name("New User")
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .build();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("New User", response.getName());
        assertEquals("EMPLOYEE", user.getRole()); // Check original logic from service
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_Failed_UsernameExists() {
        UserRequest request = UserRequest.builder()
                .username(USERNAME)
                .build();
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("failed to create user", exception.getMessage());
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals(USERNAME, responses.get(0).getUsername());
        assertNull(responses.get(0).getRole()); // Should be excluded in management response
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals(USERNAME, response.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("failed to get user", exception.getMessage());
    }

    @Test
    void updateUser_Success() {
        UserRequest request = UserRequest.builder()
                .name("Updated Name")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateUser(1L, request);

        assertEquals("Updated Name", response.getName());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void getCurrentUser_ShouldReturnOnlyTodayBookings() {
        // Arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        
        Booking todayBooking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status("CONFIRMED")
                .description("Today's booking")
                .build();

        when(bookingRepository.findByUserIdAndStartTimeBetween(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(todayBooking));

        // Act
        UserResponse response = userService.getCurrentUser(USERNAME);

        // Assert
        assertEquals(1, response.getBookings().size());
        assertEquals("Today's booking", response.getBookings().get(0).getDescription());
        
        verify(userRepository).findByUsername(USERNAME);
        verify(bookingRepository).findByUserIdAndStartTimeBetween(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
