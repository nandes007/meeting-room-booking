package com.nandestech.meetingroom.service;
 
import com.nandestech.meetingroom.dto.ApproveBookingRequest;
import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.entity.Booking;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.BookingRepository;
import com.nandestech.meetingroom.repository.RoomRepository;
import com.nandestech.meetingroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
 
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
 
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
 
    @Mock
    private BookingRepository bookingRepository;
 
    @Mock
    private UserRepository userRepository;
 
    @Mock
    private RoomRepository roomRepository;
 
    @InjectMocks
    private BookingService bookingService;
 
    private User user;
    private Booking booking;
    private final String USERNAME = "testuser";
    private final String ADMIN_ROLE = "ADMIN";
    private final String USER_ROLE = "USER";
 
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username(USERNAME)
                .role(USER_ROLE)
                .build();
 
        booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .status("pending")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
    }
 
    @Test
    void approveBooking_Success() {
        ApproveBookingRequest request = new ApproveBookingRequest("approve");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
 
        BookingResponse response = bookingService.approveBooking(1L, request, ADMIN_ROLE);
 
        assertNotNull(response);
        assertEquals("approved", response.getStatus());
    }
 
    @Test
    void approveBooking_AccessDenied() {
        ApproveBookingRequest request = new ApproveBookingRequest("approve");
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            bookingService.approveBooking(1L, request, USER_ROLE)
        );
        assertTrue(exception.getMessage().contains("Access denied"));
    }
 
    @Test
    void approveBooking_NotPending() {
        booking.setStatus("approved");
        ApproveBookingRequest request = new ApproveBookingRequest("approve");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
 
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            bookingService.approveBooking(1L, request, ADMIN_ROLE)
        );
        assertTrue(exception.getMessage().contains("Only pending bookings can be approved"));
    }
 
    @Test
    void getAllBookings_WithStatusFilter_Admin() {
        Page<Booking> page = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findByStatus(eq("pending"), any(Pageable.class))).thenReturn(page);
 
        Page<BookingResponse> responses = bookingService.getAllBookings(ADMIN_ROLE, USERNAME, 1, 10, "pending");
 
        assertEquals(1, responses.getTotalElements());
        assertEquals("pending", responses.getContent().get(0).getStatus());
    }
 
    @Test
    void getAllBookings_WithoutFilter_User() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Page<Booking> page = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(page);
 
        Page<BookingResponse> responses = bookingService.getAllBookings(USER_ROLE, USERNAME, 1, 10, null);
 
        assertEquals(1, responses.getTotalElements());
    }
}
