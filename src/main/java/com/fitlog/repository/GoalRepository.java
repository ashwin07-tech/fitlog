package com.fitlog.repository;

import com.fitlog.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdOrderByWeekStartDateDesc(Long userId);
    Optional<Goal> findTopByUserIdOrderByWeekStartDateDesc(Long userId);
    Optional<Goal> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
}
