package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Goal Entity representing weekly targets (Workouts & Calories).
 */
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "goal_type", length = 30)
    private String goalType = "WORKOUT_COUNT";

    @Column(name = "target_value")
    private Double targetValue = 4.0;

    @NotNull(message = "Week start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "target_date")
    private LocalDate targetDate;

    @NotNull(message = "Target weekly workout count is required")
    @Positive(message = "Target workout count must be at least 1")
    @Column(name = "target_workout_count", nullable = false)
    private Integer targetWorkoutCount;

    @PositiveOrZero(message = "Target calories must be non-negative")
    @Column(name = "target_calories")
    private Integer targetCalories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Goal() {
        this.weekStartDate = LocalDate.now();
        this.startDate = LocalDate.now();
        this.targetWorkoutCount = 4;
        this.targetValue = 4.0;
        this.targetCalories = 2000;
        this.goalType = "WORKOUT_COUNT";
    }

    public Goal(User user, LocalDate weekStartDate, Integer targetWorkoutCount, Integer targetCalories) {
        this.user = user;
        this.weekStartDate = weekStartDate;
        this.targetWorkoutCount = targetWorkoutCount;
        this.targetValue = targetWorkoutCount != null ? targetWorkoutCount.doubleValue() : 4.0;
        this.targetCalories = targetCalories;
        this.goalType = "WORKOUT_COUNT";
    }

    // Aliases for Class Diagram ProgressGoal
    private LocalDate startDate;

    public LocalDate getStartDate() {
        return weekStartDate != null ? weekStartDate : startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.weekStartDate = startDate;
    }

    public String getGoalType() {
        return goalType != null ? goalType : "WORKOUT_COUNT";
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public Double getTargetValue() {
        return targetValue != null ? targetValue : (targetWorkoutCount != null ? targetWorkoutCount.doubleValue() : 0.0);
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
        if (targetValue != null) {
            this.targetWorkoutCount = (int) Math.round(targetValue);
        }
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public Integer getTargetWorkoutCount() {
        return targetWorkoutCount;
    }

    public void setTargetWorkoutCount(Integer targetWorkoutCount) {
        this.targetWorkoutCount = targetWorkoutCount;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
