package com.nandestech.meetingroom.service;

import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.entity.Room;
import com.nandestech.meetingroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public RoomResponse createRoom(RoomRequest req) {
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

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return toResponse(room);
    }

    public RoomResponse updateRoom(Long id, RoomRequest req) {
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

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.delete(room);
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .name(room.getName())
                .capacity(room.getCapacity())
                .location(room.getLocation())
                .isAvailable(room.getIsAvailable())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
