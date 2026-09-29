package com.fitlog.controller;

import com.fitlog.dto.DailySummaryDto;
import com.fitlog.dto.WeeklySummaryDto;
import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.NoDataFoundException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.GoalService;
import com.fitlog.service.MealService;
import com.fitlog.service.SummaryService;
import com.fitlog.service.UserService;
import com.fitlog.service.WorkoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller Layer: DashboardController
 * Serves the primary FitLog dashboard (dashboard.html) with daily balance,
 * entity counts, weekly progress, live search filter, and active user context.
 */
@Controller
public class DashboardController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final MealService mealService;
    private final GoalService goalService;
    private final SummaryService summaryService;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;

    public DashboardController(UserService userService,
                               WorkoutService workoutService,
                               MealService mealService,
                               GoalService goalService,
                               SummaryService summaryService,
                               WorkoutRepository workoutRepository,
                               MealRepository mealRepository) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.mealService = mealService;
        this.goalService = goalService;
        this.summaryService = summaryService;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
    }

    private User getActiveUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("currentUserId");
        if (userId != null) {
            try {
                return userService.getUserById(userId);
            } catch (ResourceNotFoundException ignored) {}
        }
        User defaultUser = userService.getOrCreateDefaultUser();
        session.setAttribute("currentUserId", defaultUser.getId());
        return defaultUser;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@RequestParam(value = "date", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(value = "workoutSearch", required = false) String workoutSearch,
                            HttpSession session,
                            Model model) {
        User user = getActiveUser(session);
        LocalDate selectedDate = (date != null) ? date : LocalDate.now();

        // 1. Dashboard Total Counts (All across database as specified in Rubric "Ideas to Go Further")
        long totalUsers = userService.getTotalUsersCount();
        long totalWorkouts = workoutRepository.count();
        long totalMeals = mealRepository.count();

        // 2. Daily Calorie Balance (Burned vs Consumed)
        DailySummaryDto dailyBalance = summaryService.getDailyBalance(user.getId(), selectedDate);

        // 3. Weekly Goal & Metrics
        Goal currentGoal = goalService.getCurrentGoal(user.getId());
        WeeklySummaryDto weeklySummary = null;
        String weeklySummaryNotice = null;
        try {
            weeklySummary = summaryService.getWeeklyTrend(user.getId(), selectedDate);
        } catch (NoDataFoundException ex) {
            weeklySummaryNotice = ex.getMessage();
        }

        // 4. Workout List with Search/Filter for active user
        List<Workout> workouts = workoutService.getWorkouts(user.getId());
        if (workoutSearch != null && !workoutSearch.trim().isEmpty()) {
            String query = workoutSearch.trim().toLowerCase();
            workouts = workouts.stream()
                    .filter(w -> (w.getType() != null && w.getType().toLowerCase().contains(query)) ||
                                 (w.getNotes() != null && w.getNotes().toLowerCase().contains(query)))
                    .toList();
        }

        // 5. Meal List for active user
        List<Meal> meals = mealService.getMeals(user.getId());

        // 6. All users for Top Navbar Switcher
        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("currentUser", user);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("prevDate", selectedDate.minusDays(1));
        model.addAttribute("nextDate", selectedDate.plusDays(1));
        model.addAttribute("isToday", selectedDate.equals(LocalDate.now()));
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalWorkouts", totalWorkouts);
        model.addAttribute("totalMeals", totalMeals);
        model.addAttribute("dailyBalance", dailyBalance);
        model.addAttribute("currentGoal", currentGoal);
        model.addAttribute("weeklySummary", weeklySummary);
        model.addAttribute("weeklySummaryNotice", weeklySummaryNotice);
        model.addAttribute("workouts", workouts);
        model.addAttribute("workoutSearch", workoutSearch);
        model.addAttribute("meals", meals);

        // Models for quick modal form bindings
        Workout defaultWorkout = new Workout();
        defaultWorkout.setDate(selectedDate);
        Meal defaultMeal = new Meal();
        defaultMeal.setDate(selectedDate);

        model.addAttribute("newWorkout", defaultWorkout);
        model.addAttribute("newMeal", defaultMeal);
        model.addAttribute("newGoal", currentGoal != null ? currentGoal : new Goal());

        return "dashboard";
    }
}
