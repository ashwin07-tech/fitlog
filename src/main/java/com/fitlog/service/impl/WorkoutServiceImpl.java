package com.fitlog.service.impl;

import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.UserRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.WorkoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Workout logWorkout(Workout workout, Long userId) {
        // Validation: Workout type
        if (workout.getType() == null || workout.getType().trim().isEmpty()) {
            throw new BusinessRuleException("Workout type is required.");
        }

        // Validation: Duration must be greater than zero
        if (workout.getDuration() == null || workout.getDuration() <= 0) {
            throw new BusinessRuleException("Workout duration must be greater than 0 minutes.");
        }

        // Service-layer Validation: Reject negative numbers for calories
        if (workout.getCaloriesBurnt() == null || workout.getCaloriesBurnt() < 0) {
            throw new BusinessRuleException("Calories burnt must be a non-negative number (0 or greater). Received: "
                    + workout.getCaloriesBurnt());
        }

        if (workout.getDate() == null) {
            workout.setDate(LocalDate.now());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        workout.setUser(user);
        return workoutRepository.save(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workout> getWorkouts(Long userId) {
        return workoutRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workout> getWorkoutsByDate(Long userId, LocalDate date) {
        return workoutRepository.findByUserIdAndDate(userId, date);
    }
}
