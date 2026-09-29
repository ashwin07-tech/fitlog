package com.fitlog.repository;

import com.fitlog.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserIdOrderByDateDesc(Long userId);
    List<Workout> findByUserIdAndDate(Long userId, LocalDate date);
    List<Workout> findByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
    long countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
