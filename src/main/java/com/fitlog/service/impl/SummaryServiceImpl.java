package com.fitlog.service.impl;

import com.fitlog.dto.DailySummaryDto;
import com.fitlog.dto.WeeklySummaryDto;
import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.Workout;
import com.fitlog.exception.NoDataFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.GoalService;
import com.fitlog.service.SummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SummaryServiceImpl implements SummaryService {

    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final GoalService goalService;

    public SummaryServiceImpl(WorkoutRepository workoutRepository, MealRepository mealRepository, GoalService goalService) {
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.goalService = goalService;
    }

    @Override
    public DailySummaryDto getDailyBalance(Long userId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<Workout> workouts = workoutRepository.findByUserIdAndDate(userId, targetDate);
        List<Meal> meals = mealRepository.findByUserIdAndDate(userId, targetDate);

        int totalBurnt = workouts.stream().mapToInt(Workout::getCaloriesBurnt).sum();
        int totalConsumed = meals.stream().mapToInt(Meal::getCalories).sum();

        return new DailySummaryDto(targetDate, totalBurnt, totalConsumed, workouts, meals);
    }

    @Override
    public WeeklySummaryDto getWeeklyTrend(Long userId, LocalDate dateInWeek) {
        LocalDate anchorDate = (dateInWeek != null) ? dateInWeek : LocalDate.now();
        LocalDate weekStart = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Workout> workouts = workoutRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, weekStart, weekEnd);
        List<Meal> meals = mealRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, weekStart, weekEnd);

        // Core MVP Rule: Conditioned on having at least one entry for that week
        if (workouts.isEmpty() && meals.isEmpty()) {
            throw new NoDataFoundException(
                    "No entries found for the week of " + weekStart + " to " + weekEnd +
                    ". A weekly summary can only be generated once at least one workout or meal entry exists for that week."
            );
        }

        int totalWorkouts = workouts.size();
        int totalBurnt = workouts.stream().mapToInt(Workout::getCaloriesBurnt).sum();
        int totalConsumed = meals.stream().mapToInt(Meal::getCalories).sum();

        Goal currentGoal = goalService.getCurrentGoal(userId);
        int target = (currentGoal != null && currentGoal.getTargetWorkoutCount() != null)
                ? currentGoal.getTargetWorkoutCount() : 4;
        boolean goalMet = totalWorkouts >= target;

        return new WeeklySummaryDto(weekStart, weekEnd, totalWorkouts, target, goalMet, totalBurnt, totalConsumed, workouts);
    }
}
