package com.fitlog.controller;

import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.service.GoalService;
import com.fitlog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Controller Layer: UserController (and AuthController)
 * Handles Profile Management, BMR calculation, and User Switching / Registration.
 */
@Controller
public class UserController {

    private final UserService userService;
    private final GoalService goalService;

    public UserController(UserService userService, GoalService goalService) {
        this.userService = userService;
        this.goalService = goalService;
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

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = getActiveUser(session);
        List<User> allUsers = userService.getAllUsers();
        Goal currentGoal = goalService.getCurrentGoal(user.getId());

        int age = 25;
        if (user.getBirthDate() != null) {
            age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        }

        double bmr = user.calculateBMR();

        model.addAttribute("currentUser", user);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("currentGoal", currentGoal);
        model.addAttribute("userAge", age);
        model.addAttribute("calculatedBMR", Math.round(bmr));

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam(value = "currentWeight", required = false, defaultValue = "70.0") Double currentWeight,
                                @RequestParam(value = "birthDate", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = getActiveUser(session);
            userService.updateProfile(user.getId(), name, currentWeight, birthDate);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully! BMR recalculated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + ex.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/users/switch")
    public String switchUser(@RequestParam("userId") Long userId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(userId);
            session.setAttribute("currentUserId", user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Switched active user to: " + user.getName());
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/users/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("name") String name,
                               @RequestParam(value = "currentWeight", defaultValue = "70.0") Double currentWeight,
                               @RequestParam(value = "birthDate", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            LocalDate bDate = birthDate != null ? birthDate : LocalDate.of(1998, 1, 1);
            User newUser = new User(username, email, name, currentWeight, bDate);
            User saved = userService.createUser(newUser);
            session.setAttribute("currentUserId", saved.getId());
            redirectAttributes.addFlashAttribute("successMessage", "New user '" + saved.getName() + "' created and set as active!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not register user: " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("currentUser", getActiveUser(session));
        return "profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("currentUserId");
        redirectAttributes.addFlashAttribute("successMessage", "Session reset to default user.");
        return "redirect:/dashboard";
    }
}
