package com.fitlog.service;

import com.fitlog.entity.Goal;

import java.time.LocalDate;

public interface GoalService {
    Goal setGoal(Integer targetWorkoutCount, Integer targetCalories, Long userId, LocalDate weekStartDate);
    Goal setGoal(String goalType, Double targetValue, Integer targetCalories, Long userId, LocalDate weekStartDate);
    Goal getCurrentGoal(Long userId);
}
