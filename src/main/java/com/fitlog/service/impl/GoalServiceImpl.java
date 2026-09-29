package com.fitlog.service.impl;

import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.service.GoalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalServiceImpl(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Goal setGoal(Integer targetWorkoutCount, Integer targetCalories, Long userId, LocalDate weekStartDate) {
        if (targetWorkoutCount == null || targetWorkoutCount <= 0) {
            throw new BusinessRuleException("Target workout count must be at least 1 workout per week.");
        }

        if (targetCalories != null && targetCalories < 0) {
            throw new BusinessRuleException("Target calories must be a non-negative number.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDate normalizedStart = weekStartDate != null
                ? weekStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Goal goal = goalRepository.findByUserIdAndWeekStartDate(userId, normalizedStart)
                .orElse(new Goal(user, normalizedStart, targetWorkoutCount, targetCalories));

        goal.setTargetWorkoutCount(targetWorkoutCount);
        goal.setTargetValue(targetWorkoutCount.doubleValue());
        goal.setGoalType("WORKOUT_COUNT");
        goal.setTargetCalories(targetCalories != null ? targetCalories : 0);
        goal.setUser(user);

        return goalRepository.save(goal);
    }

    @Override
    public Goal setGoal(String goalType, Double targetValue, Integer targetCalories, Long userId, LocalDate weekStartDate) {
        int workoutCount = (targetValue != null && targetValue > 0) ? (int) Math.round(targetValue) : 4;
        Goal goal = setGoal(workoutCount, targetCalories, userId, weekStartDate);
        if (goalType != null && !goalType.trim().isEmpty()) {
            goal.setGoalType(goalType.trim());
        }
        if (targetValue != null) {
            goal.setTargetValue(targetValue);
        }
        return goalRepository.save(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public Goal getCurrentGoal(Long userId) {
        return goalRepository.findTopByUserIdOrderByWeekStartDateDesc(userId)
                .orElseGet(() -> {
                    LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    Goal defaultGoal = new Goal();
                    defaultGoal.setWeekStartDate(currentMonday);
                    defaultGoal.setTargetWorkoutCount(4);
                    defaultGoal.setTargetCalories(2000);
                    return defaultGoal;
                });
    }
}
