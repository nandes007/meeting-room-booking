package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.entity.Room;
import com.nandestech.meetingroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public RoomResponse createRoom(RoomRequest req, String role) {
        validateAdmin(role);
        Room room = Room.builder()
                .name(req.getName())
                .capacity(req.getCapacity())
                .location(req.getLocation())
                .isAvailable(req.getIsAvailable())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        room = roomRepository.save(room);
        return toResponse(room);
    }

    public Page<RoomResponse> getAllRooms(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return roomRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return toResponse(room);
    }

    public RoomResponse updateRoom(Long id, RoomRequest req, String role) {
        validateAdmin(role);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setName(req.getName());
        room.setCapacity(req.getCapacity());
        room.setLocation(req.getLocation());
        room.setIsAvailable(req.getIsAvailable());
        room.setUpdatedAt(LocalDateTime.now());

        room = roomRepository.save(room);
        return toResponse(room);
    }

    public void deleteRoom(Long id, String role) {
        validateAdmin(role);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.delete(room);
    }

    private void validateAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPERADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: Only administrators can perform this action");
        }
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .location(room.getLocation())
                .isAvailable(room.getIsAvailable())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
