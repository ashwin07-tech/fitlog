package com.fitlog.controller;

import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.UserService;
import com.fitlog.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller Layer: WorkoutController
 * Handles workout logging view (log-workout.html) and CRUD actions.
 */
@Controller
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserService userService;
    private final WorkoutRepository workoutRepository;

    public WorkoutController(WorkoutService workoutService, UserService userService, WorkoutRepository workoutRepository) {
        this.workoutService = workoutService;
        this.userService = userService;
        this.workoutRepository = workoutRepository;
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

    @GetMapping({"/workouts", "/workouts/log"})
    public String logWorkoutPage(@RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "date", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 HttpSession session,
                                 Model model) {
        User user = getActiveUser(session);
        List<Workout> workouts = workoutService.getWorkouts(user.getId());

        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            workouts = workouts.stream()
                    .filter(w -> (w.getType() != null && w.getType().toLowerCase().contains(q)) ||
                                 (w.getNotes() != null && w.getNotes().toLowerCase().contains(q)))
                    .toList();
        }

        if (date != null) {
            workouts = workouts.stream()
                    .filter(w -> date.equals(w.getDate()))
                    .toList();
        }

        Workout newWorkout = new Workout();
        if (date != null) {
            newWorkout.setDate(date);
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("workouts", workouts);
        model.addAttribute("newWorkout", newWorkout);
        model.addAttribute("search", search);
        model.addAttribute("selectedDate", date != null ? date : LocalDate.now());

        return "log-workout";
    }

    @PostMapping({"/workouts", "/workouts/log"})
    public String logWorkout(@ModelAttribute("newWorkout") Workout workout,
                             HttpServletRequest request,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = getActiveUser(session);
            workoutService.logWorkout(workout, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Workout logged successfully!");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/workouts")) {
            return "redirect:/workouts/log";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/workouts/delete/{id}")
    public String deleteWorkout(@PathVariable Long id,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        workoutRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Workout removed successfully.");
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/workouts")) {
            return "redirect:/workouts/log";
        }
        return "redirect:/dashboard";
    }
}
