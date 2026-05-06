package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.BookingRequest;
import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.entity.Booking;
import com.nandestech.meetingroom.entity.Room;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.BookingRepository;
import com.nandestech.meetingroom.repository.RoomRepository;
import com.nandestech.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public BookingResponse createBooking(BookingRequest req, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateBookingTime(req.getStartTime(), req.getEndTime());

        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getIsAvailable()) {
            throw new RuntimeException("Room is not available for booking");
        }

        if (bookingRepository.existsOverlappingBooking(req.getRoomId(), req.getStartTime(), req.getEndTime())) {
            throw new RuntimeException("The room is already booked for the selected time slot");
        }

        Booking booking = Booking.builder()
                .userId(user.getId())
                .roomId(req.getRoomId())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status("pending")
                .description(req.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    public Page<BookingResponse> getAllBookings(String role, String username, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        if ("ADMIN".equalsIgnoreCase(role) || "SUPERADMIN".equalsIgnoreCase(role)) {
            return bookingRepository.findAll(pageable)
                    .map(this::toResponse);
        } else {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return bookingRepository.findByUserId(user.getId(), pageable)
                    .map(this::toResponse);
        }
    }

    public BookingResponse getBookingById(Long id, String role, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPERADMIN".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!booking.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access denied: You can only view your own bookings");
            }
        }
        
        return toResponse(booking);
    }

    public BookingResponse updateBooking(Long id, BookingRequest req, String username, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPERADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: Only administrators can edit bookings");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        validateBookingTime(req.getStartTime(), req.getEndTime());

        if (bookingRepository.existsOverlappingBookingExcluding(req.getRoomId(), id, req.getStartTime(), req.getEndTime())) {
            throw new RuntimeException("The room is already booked for the selected time slot");
        }

        booking.setRoomId(req.getRoomId());
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        if (req.getStatus() != null) {
            booking.setStatus(req.getStatus());
        }
        booking.setDescription(req.getDescription());
        booking.setUpdatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    public void deleteBooking(Long id, String username, String role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPERADMIN".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!booking.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access denied: You can only cancel your own bookings");
            }
        }

        bookingRepository.delete(booking);
    }

    private void validateBookingTime(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Booking cannot be made for past dates");
        }
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new RuntimeException("End time must be after start time");
        }
    }

    private BookingResponse toResponse(Booking booking) {
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
