package com.fitlog.dto;

import com.fitlog.entity.Meal;
import com.fitlog.entity.Workout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Daily Log Summary DTO matching the Class Diagram.
 */
public class DailySummaryDto {

    private LocalDate date;
    private int totalCaloriesBurnt;
    private int totalCaloriesConsumed;
    private int netCalories;
    private List<Workout> workouts = new ArrayList<>();
    private List<Meal> meals = new ArrayList<>();

    public DailySummaryDto() {
    }

    public DailySummaryDto(LocalDate date, int totalCaloriesBurnt, int totalCaloriesConsumed, List<Workout> workouts, List<Meal> meals) {
        this.date = date;
        this.totalCaloriesBurnt = totalCaloriesBurnt;
        this.totalCaloriesConsumed = totalCaloriesConsumed;
        this.netCalories = totalCaloriesConsumed - totalCaloriesBurnt;
        this.workouts = workouts != null ? workouts : new ArrayList<>();
        this.meals = meals != null ? meals : new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getNetCalories() {
        return netCalories;
    }

    public void setNetCalories(int netCalories) {
        this.netCalories = netCalories;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    // Aliases for view templates
    public int getCaloriesBurned() {
        return totalCaloriesBurnt;
    }

    public int getCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public int getWorkoutsCount() {
        return workouts != null ? workouts.size() : 0;
    }

    public int getMealsCount() {
        return meals != null ? meals.size() : 0;
    }
}
