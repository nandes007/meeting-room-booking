package com.nandestech.meetingroom.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
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
