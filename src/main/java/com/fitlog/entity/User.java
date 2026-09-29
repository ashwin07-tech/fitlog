package com.fitlog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Entity representing a fitness tracker profile.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(name = "password_hash", length = 255)
    private String passwordHash = "demo123";

    @Column(name = "birth_date")
    private java.time.LocalDate birthDate = java.time.LocalDate.of(1998, 1, 1);

    @Column(name = "current_weight")
    private Double currentWeight = 70.0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workout> workouts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goals = new ArrayList<>();

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.name = username;
    }

    public User(String username, String email, String name, Double currentWeight, java.time.LocalDate birthDate) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.currentWeight = currentWeight;
        this.birthDate = birthDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.name == null || this.name.trim().isEmpty()) {
            this.name = this.username;
        }
        ensureUsernameValid();
    }

    @PreUpdate
    protected void onUpdate() {
        ensureUsernameValid();
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    private void ensureUsernameValid() {
        if (this.username == null || this.username.trim().isEmpty()) {
            if (this.email != null && this.email.contains("@")) {
                this.username = this.email.split("@")[0];
            } else if (this.name != null && !this.name.trim().isEmpty()) {
                this.username = this.name.trim().toLowerCase().replaceAll("\\s+", "_");
            } else {
                this.username = "user_" + (this.id != null ? this.id : System.currentTimeMillis() % 10000);
            }
        }
    }

    /**
     * Calculates Basal Metabolic Rate (BMR) using the Mifflin-St Jeor formula:
     * BMR = (10 * weight in kg) + (6.25 * height in cm, assumed 175) - (5 * age in years) + 5
     */
    public double calculateBMR() {
        double weight = (currentWeight != null && currentWeight > 0) ? currentWeight : 70.0;
        int age = 25;
        if (birthDate != null) {
            age = java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
            if (age <= 0) age = 25;
        }
        // Baseline BMR calculation
        return (10 * weight) + (6.25 * 175.0) - (5 * age) + 5;
    }

    public void setGoal(Goal goal) {
        addGoal(goal);
    }

    // Helper methods for relationships
    public void addWorkout(Workout workout) {
        workouts.add(workout);
        workout.setUser(this);
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
        meal.setUser(this);
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
        goal.setUser(this);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name != null ? name : username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public java.time.LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(java.time.LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }
}
