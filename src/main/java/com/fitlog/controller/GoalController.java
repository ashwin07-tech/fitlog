package com.fitlog.controller;

import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.service.GoalService;
import com.fitlog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Controller Layer: GoalController
 * Handles setting and updating personal weekly workout goals & targets.
 */
@Controller
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
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

    @PostMapping("/goals")
    public String setGoal(@RequestParam(value = "targetWorkoutCount", required = false) Integer targetWorkoutCount,
                          @RequestParam(value = "targetCalories", required = false, defaultValue = "2000") Integer targetCalories,
                          @RequestParam(value = "goalType", required = false, defaultValue = "WORKOUT_COUNT") String goalType,
                          @RequestParam(value = "targetValue", required = false) Double targetValue,
                          @RequestParam(value = "weekStartDate", required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
                          HttpServletRequest request,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = getActiveUser(session);
            Integer workoutCount = targetWorkoutCount;
            if (workoutCount == null && targetValue != null) {
                workoutCount = (int) Math.round(targetValue);
            }
            if (workoutCount == null) {
                workoutCount = 4;
            }

            Goal goal = goalService.setGoal(workoutCount, targetCalories, user.getId(), weekStartDate);
            if (goalType != null) {
                goal.setGoalType(goalType);
            }
            if (targetValue != null) {
                goal.setTargetValue(targetValue);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Weekly Goal set: " + goal.getTargetWorkoutCount() + " workouts / week (Target: " + goal.getTargetCalories() + " kcal)");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/profile")) {
            return "redirect:/profile";
        }
        return "redirect:/dashboard";
    }
}
