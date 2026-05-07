package com.nandestech.meetingroom.repository;

import com.nandestech.meetingroom.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.roomId = :roomId AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.roomId = :roomId AND b.id <> :excludeId AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsOverlappingBookingExcluding(@Param("roomId") Long roomId, @Param("excludeId") Long excludeId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Page<Booking> findByUserId(Long userId, Pageable pageable);
    java.util.List<Booking> findByUserId(Long userId);
    java.util.List<Booking> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
