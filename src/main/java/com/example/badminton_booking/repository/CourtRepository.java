package com.example.badminton_booking.repository;

import com.example.badminton_booking.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Integer> {
    List<Court> findByIsActiveTrue();
}

