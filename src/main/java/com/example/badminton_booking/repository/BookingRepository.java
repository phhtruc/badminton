package com.example.badminton_booking.repository;

import com.example.badminton_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND b.status = 'APPROVED'")
    List<Booking> findApprovedBookingsByCourtAndDateRange(
        @Param("courtId") Integer courtId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND b.status = 'APPROVED'")
    List<Booking> findApprovedBookingsByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<Booking> findByStatus(String status);

    List<Booking> findByUserId(UUID userId);
}

