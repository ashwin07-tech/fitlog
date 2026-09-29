package com.fitlog.service.impl;

import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.service.MealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealServiceImpl(MealRepository mealRepository, UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Meal logMeal(Meal meal, Long userId) {
        if (meal.getFoodItem() == null || meal.getFoodItem().trim().isEmpty()) {
            throw new BusinessRuleException("Food item name is required.");
        }

        if (meal.getQuantity() == null || meal.getQuantity().trim().isEmpty()) {
            throw new BusinessRuleException("Quantity is required (e.g., 1 bowl, 200g, 1 apple).");
        }

        // Service-layer Validation: Reject negative numbers for calories
        if (meal.getCalories() == null || meal.getCalories() < 0) {
            throw new BusinessRuleException("Calories consumed must be a non-negative number (0 or greater). Received: "
                    + meal.getCalories());
        }

        if (meal.getDate() == null) {
            meal.setDate(LocalDate.now());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        meal.setUser(user);
        return mealRepository.save(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getMeals(Long userId) {
        return mealRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getMealsByDate(Long userId, LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date);
    }
}
