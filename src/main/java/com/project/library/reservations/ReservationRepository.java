package com.project.library.reservations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<List<Reservation>> findByUserId(Long id);
    Optional<List<Reservation>> findByUserIdAndBookId(Long userId, Long bookId);
}

