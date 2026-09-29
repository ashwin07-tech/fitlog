package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Meal Entity matching the Class Diagram.
 */
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Food item is required")
    @Column(name = "food_item", nullable = false, length = 150)
    private String foodItem;

    @NotBlank(message = "Quantity is required")
    @Column(nullable = false, length = 50)
    private String quantity;

    @NotNull(message = "Calories is required")
    @PositiveOrZero(message = "Calories must be a non-negative number")
    @Column(nullable = false)
    private Integer calories;

    @NotNull(message = "Date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Meal() {
        this.date = LocalDate.now();
    }

    public Meal(String foodItem, String quantity, Integer calories, LocalDate date) {
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.date = date != null ? date : LocalDate.now();
    }

    public Meal(User user, String foodItem, String quantity, Integer calories, LocalDate date) {
        this.user = user;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.date = date != null ? date : LocalDate.now();
    }

    // Method matching Class Diagram MealLog.logMeal()
    public void logMeal() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    // Aliases matching Class Diagram MealItem & MealLog
    public String getMealName() {
        return foodItem;
    }

    public void setMealName(String mealName) {
        this.foodItem = mealName;
    }

    public String getServingSize() {
        return quantity;
    }

    public void setServingSize(String servingSize) {
        this.quantity = servingSize;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
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
