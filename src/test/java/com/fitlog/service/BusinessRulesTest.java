package com.fitlog.service;

import com.fitlog.dto.WeeklySummaryDto;
import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.NoDataFoundException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.impl.GoalServiceImpl;
import com.fitlog.service.impl.MealServiceImpl;
import com.fitlog.service.impl.SummaryServiceImpl;
import com.fitlog.service.impl.UserServiceImpl;
import com.fitlog.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessRulesTest {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private MealRepository mealRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalService goalService;

    private WorkoutService workoutService;
    private MealService mealService;
    private SummaryService summaryService;
    private User testUser;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutServiceImpl(workoutRepository, userRepository);
        mealService = new MealServiceImpl(mealRepository, userRepository);
        summaryService = new SummaryServiceImpl(workoutRepository, mealRepository, goalService);

        testUser = new User("alex_runner", "alex@example.com");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Validation & Integrity: Reject negative numbers for calories burned")
    void shouldRejectNegativeCaloriesBurnt() {
        Workout invalidWorkout = new Workout();
        invalidWorkout.setType("Run");
        invalidWorkout.setDuration(30);
        invalidWorkout.setCaloriesBurnt(-100); // Negative calories!

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            workoutService.logWorkout(invalidWorkout, 1L);
        });

        assertTrue(exception.getMessage().contains("Calories burnt must be a non-negative number"));
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("Validation & Integrity: Reject duration less than or equal to zero")
    void shouldRejectZeroOrNegativeDuration() {
        Workout invalidWorkout = new Workout();
        invalidWorkout.setType("Run");
        invalidWorkout.setDuration(0); // 0 duration!
        invalidWorkout.setCaloriesBurnt(200);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            workoutService.logWorkout(invalidWorkout, 1L);
        });

        assertTrue(exception.getMessage().contains("Workout duration must be greater than 0 minutes"));
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("Validation & Integrity: Reject negative numbers for calories consumed")
    void shouldRejectNegativeCaloriesConsumed() {
        Meal invalidMeal = new Meal();
        invalidMeal.setFoodItem("Apple");
        invalidMeal.setQuantity("1 medium");
        invalidMeal.setCalories(-50); // Negative calories!

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            mealService.logMeal(invalidMeal, 1L);
        });

        assertTrue(exception.getMessage().contains("Calories consumed must be a non-negative number"));
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    @DisplayName("Weekly Metrics: Weekly summary is conditioned on having at least one entry for that week")
    void shouldThrowNoDataFoundExceptionWhenWeekIsEmpty() {
        LocalDate testMonday = LocalDate.of(2026, 9, 28);
        LocalDate testSunday = testMonday.plusDays(6);

        // When no workouts and no meals exist in that week
        when(workoutRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, testMonday, testSunday))
                .thenReturn(Collections.emptyList());
        when(mealRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, testMonday, testSunday))
                .thenReturn(Collections.emptyList());

        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () -> {
            summaryService.getWeeklyTrend(1L, testMonday);
        });

        assertTrue(exception.getMessage().contains("once at least one workout or meal entry exists for that week"));
    }

    @Test
    @DisplayName("Weekly Metrics: Successfully calculates weekly summary when entry exists")
    void shouldGenerateWeeklySummaryWhenEntryExists() {
        LocalDate testMonday = LocalDate.of(2026, 9, 28);
        LocalDate testSunday = testMonday.plusDays(6);

        Workout sampleWorkout = new Workout(testUser, "Run", 45, 300, testMonday);
        when(workoutRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, testMonday, testSunday))
                .thenReturn(List.of(sampleWorkout));
        when(mealRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, testMonday, testSunday))
                .thenReturn(Collections.emptyList());

        Goal sampleGoal = new Goal(testUser, testMonday, 4, 1500);
        when(goalService.getCurrentGoal(1L)).thenReturn(sampleGoal);

        WeeklySummaryDto trend = summaryService.getWeeklyTrend(1L, testMonday);

        assertNotNull(trend);
        assertEquals(1, trend.getTotalWorkoutsCompleted());
        assertEquals(1, trend.getWorkoutsCompleted());
        assertEquals(300, trend.getTotalCaloriesBurnt());
        assertEquals(300, trend.getTotalCaloriesBurned());
        assertEquals(4, trend.getTargetWorkouts());
        assertFalse(trend.isGoalMet());
    }

    @Test
    @DisplayName("User Entity: Calculate BMR using Mifflin-St Jeor equation")
    void shouldCalculateCorrectBMR() {
        User user = new User("alex_runner", "alex@example.com", "Alex Mercer", 72.5, LocalDate.of(1998, 5, 14));
        double bmr = user.calculateBMR();
        assertTrue(bmr > 1500 && bmr < 2200, "BMR should be in normal adult range");
    }
}
