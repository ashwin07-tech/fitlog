package com.fitlog.controller;

import com.fitlog.dto.DailySummaryDto;
import com.fitlog.dto.WeeklySummaryDto;
import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.service.GoalService;
import com.fitlog.service.MealService;
import com.fitlog.service.SummaryService;
import com.fitlog.service.UserService;
import com.fitlog.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST API Controller delivering the Core MVP Deliverables:
 * 1. Workout Tracking: log workouts with type, duration, calories burned.
 * 2. Meal Tracking: log meals with foodItem, quantity, calories.
 * 3. Daily Balance: total calories burned vs consumed for a date.
 * 4. Weekly Metrics: set/view weekly workout goal and retrieve weekly summaries.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "FitLog API", description = "REST APIs for Workouts, Meals, Daily Balance, and Weekly Goals")
public class ApiController {

    private final WorkoutService workoutService;
    private final MealService mealService;
    private final GoalService goalService;
    private final SummaryService summaryService;
    private final UserService userService;

    public ApiController(WorkoutService workoutService,
                         MealService mealService,
                         GoalService goalService,
                         SummaryService summaryService,
                         UserService userService) {
        this.workoutService = workoutService;
        this.mealService = mealService;
        this.goalService = goalService;
        this.summaryService = summaryService;
        this.userService = userService;
    }

    private Long getDefaultUserId() {
        return userService.getOrCreateDefaultUser().getId();
    }

    // -------------------------------------------------------------
    // 1. WORKOUT TRACKING
    // -------------------------------------------------------------

    @Operation(summary = "Log a workout session", description = "Records a workout with type, duration, calories burned, and optional notes. Enforces non-negative calories.")
    @PostMapping("/workouts")
    public ResponseEntity<Workout> logWorkout(@Valid @RequestBody Workout workout) {
        Workout saved = workoutService.logWorkout(workout, getDefaultUserId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "List all workouts", description = "Retrieves all logged workout sessions for the active user.")
    @GetMapping("/workouts")
    public ResponseEntity<List<Workout>> getWorkouts() {
        List<Workout> list = workoutService.getWorkouts(getDefaultUserId());
        return ResponseEntity.ok(list);
    }

    // -------------------------------------------------------------
    // 2. MEAL TRACKING
    // -------------------------------------------------------------

    @Operation(summary = "Log a meal item", description = "Records a meal with food item name, quantity/portion, and calories. Enforces non-negative calories.")
    @PostMapping("/meals")
    public ResponseEntity<Meal> logMeal(@Valid @RequestBody Meal meal) {
        Meal saved = mealService.logMeal(meal, getDefaultUserId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "List all meals", description = "Retrieves all logged meal entries for the active user.")
    @GetMapping("/meals")
    public ResponseEntity<List<Meal>> getMeals() {
        List<Meal> list = mealService.getMeals(getDefaultUserId());
        return ResponseEntity.ok(list);
    }

    // -------------------------------------------------------------
    // 3. DAILY BALANCE
    // -------------------------------------------------------------

    @Operation(summary = "Get daily calorie balance", description = "Returns total calories burned vs consumed and net balance for a specified date.")
    @GetMapping("/summary/daily")
    public ResponseEntity<DailySummaryDto> getDailyBalance(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        DailySummaryDto balance = summaryService.getDailyBalance(getDefaultUserId(), target);
        return ResponseEntity.ok(balance);
    }

    // -------------------------------------------------------------
    // 4. WEEKLY METRICS & GOALS
    // -------------------------------------------------------------

    @Operation(summary = "Set personal weekly goal", description = "Configures weekly target workout count and target calories.")
    @PostMapping("/goals")
    public ResponseEntity<Goal> setGoal(@RequestBody Goal goal) {
        Goal saved = goalService.setGoal(
                goal.getTargetWorkoutCount(),
                goal.getTargetCalories(),
                getDefaultUserId(),
                goal.getWeekStartDate()
        );
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Get current weekly goal", description = "Retrieves the active weekly workout and calorie goal.")
    @GetMapping("/goals")
    public ResponseEntity<Goal> getGoal() {
        Goal goal = goalService.getCurrentGoal(getDefaultUserId());
        return ResponseEntity.ok(goal);
    }

    @Operation(summary = "Get weekly trend summary", description = "Returns weekly workouts completed vs goal. Enforces rule: requires at least one entry.")
    @GetMapping("/summary/weekly")
    public ResponseEntity<WeeklySummaryDto> getWeeklyTrend(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        WeeklySummaryDto trend = summaryService.getWeeklyTrend(getDefaultUserId(), target);
        return ResponseEntity.ok(trend);
    }

    // -------------------------------------------------------------
    // 5. USER & BMR PROFILES
    // -------------------------------------------------------------

    @Operation(summary = "List all registered users", description = "Returns all user accounts in the database.")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get user profile by ID", description = "Returns user profile details including weight and birth date.")
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Calculate User BMR", description = "Calculates Basal Metabolic Rate using the Mifflin-St Jeor equation.")
    @GetMapping("/users/{id}/bmr")
    public ResponseEntity<Double> getUserBMR(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user.calculateBMR());
    }
}
