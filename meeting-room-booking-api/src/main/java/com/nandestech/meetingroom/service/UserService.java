package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.dto.UserRequest;
import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.entity.Booking;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.BookingRepository;
import com.nandestech.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("failed to create user");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("failed to create user");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("EMPLOYEE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return toManagementResponse(userRepository.save(user));
    }

    public Page<UserResponse> getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return userRepository.findAll(pageable)
                .map(this::toManagementResponse);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("failed to get user"));
        return toManagementResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("failed to update user"));
        
        if (request.getName() != null) user.setName(request.getName());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        
        user.setUpdatedAt(LocalDateTime.now());
        return toManagementResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("failed to delete user"));
        userRepository.delete(user);
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("failed to get current user"));
        return toResponse(user);
    }

    private UserResponse toManagementResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
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
