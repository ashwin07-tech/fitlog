package com.fitlog.service;

import com.fitlog.entity.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealService {
    Meal logMeal(Meal meal, Long userId);
    List<Meal> getMeals(Long userId);
    List<Meal> getMealsByDate(Long userId, LocalDate date);
}
