package com.fitlog.dto;

import com.fitlog.entity.Workout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Weekly Summary Metrics DTO.
 */
public class WeeklySummaryDto {

    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private int totalWorkoutsCompleted;
    private int targetWorkouts;
    private boolean goalMet;
    private int totalCaloriesBurnt;
    private int totalCaloriesConsumed;
    private List<Workout> workouts = new ArrayList<>();

    public WeeklySummaryDto() {
    }

    public WeeklySummaryDto(LocalDate weekStartDate,
                            LocalDate weekEndDate,
                            int totalWorkoutsCompleted,
                            int targetWorkouts,
                            boolean goalMet,
                            int totalCaloriesBurnt,
                            int totalCaloriesConsumed,
                            List<Workout> workouts) {
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.totalWorkoutsCompleted = totalWorkoutsCompleted;
        this.targetWorkouts = targetWorkouts;
        this.goalMet = goalMet;
        this.totalCaloriesBurnt = totalCaloriesBurnt;
        this.totalCaloriesConsumed = totalCaloriesConsumed;
        this.workouts = workouts != null ? workouts : new ArrayList<>();
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public int getTotalWorkoutsCompleted() {
        return totalWorkoutsCompleted;
    }

    public void setTotalWorkoutsCompleted(int totalWorkoutsCompleted) {
        this.totalWorkoutsCompleted = totalWorkoutsCompleted;
    }

    public int getTargetWorkouts() {
        return targetWorkouts;
    }

    public void setTargetWorkouts(int targetWorkouts) {
        this.targetWorkouts = targetWorkouts;
    }

    public boolean isGoalMet() {
        return goalMet;
    }

    public void setGoalMet(boolean goalMet) {
        this.goalMet = goalMet;
    }

    public int getTotalCaloriesBurnt() {
        return totalCaloriesBurnt;
    }

    public void setTotalCaloriesBurnt(int totalCaloriesBurnt) {
        this.totalCaloriesBurnt = totalCaloriesBurnt;
    }

    public int getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(int totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    // Aliases for view templates
    public int getWorkoutsCompleted() {
        return totalWorkoutsCompleted;
    }

    public int getTotalCaloriesBurned() {
        return totalCaloriesBurnt;
    }
}
