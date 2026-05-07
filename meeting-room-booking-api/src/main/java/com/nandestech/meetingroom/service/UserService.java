package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.entity.Booking;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.BookingRepository;
import com.nandestech.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        return userRepository.save(user);
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("failed to get current user"));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        List<BookingResponse> bookings = bookingRepository.findByUserIdAndStartTimeBetween(user.getId(), startOfToday, endOfToday)
                .stream()
                .map(this::toBookingResponse)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .bookings(bookings)
                .build();
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .description(booking.getDescription())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
