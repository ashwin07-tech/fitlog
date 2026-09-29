package com.fitlog.service;

import com.fitlog.entity.Workout;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutService {
    Workout logWorkout(Workout workout, Long userId);
    List<Workout> getWorkouts(Long userId);
    List<Workout> getWorkoutsByDate(Long userId, LocalDate date);
}
