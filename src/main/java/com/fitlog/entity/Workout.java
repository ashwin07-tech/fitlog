package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Workout Entity matching the Class Diagram.
 */
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Workout type is required")
    @Column(nullable = false, length = 50)
    private String type;

    @NotNull(message = "Duration in minutes is required")
    @Positive(message = "Duration must be greater than 0")
    @Column(nullable = false)
    private Integer duration;

    @NotNull(message = "Calories burnt is required")
    @PositiveOrZero(message = "Calories burnt must be a non-negative number")
    @Column(name = "calories_burnt", nullable = false)
    private Integer caloriesBurnt;

    @NotNull(message = "Date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Workout() {
        this.date = LocalDate.now();
    }

    public Workout(String type, Integer duration, Integer caloriesBurnt, LocalDate date) {
        this.type = type;
        this.duration = duration;
        this.caloriesBurnt = caloriesBurnt;
        this.date = date != null ? date : LocalDate.now();
    }

    public Workout(User user, String type, Integer duration, Integer caloriesBurnt, LocalDate date, String notes) {
        this.user = user;
        this.type = type;
        this.duration = duration;
        this.caloriesBurnt = caloriesBurnt;
        this.date = date != null ? date : LocalDate.now();
        this.notes = notes;
    }

    public Workout(User user, String type, Integer duration, Integer caloriesBurnt, LocalDate date) {
        this(user, type, duration, caloriesBurnt, date, null);
    }

    // Method matching class diagram
    public void logWorkout() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    public Integer getDurationMinutes() {
        return duration;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.duration = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Method matching class diagram
    public Integer getCalories() {
        return caloriesBurnt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public void setCaloriesBurnt(Integer caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
