package com.fitlog.controller;

import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.service.MealService;
import com.fitlog.service.UserService;
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
 * Controller Layer: MealController (MealLogController)
 * Handles meal logging view (log-meal.html) and CRUD actions.
 */
@Controller
public class MealController {

    private final MealService mealService;
    private final UserService userService;
    private final MealRepository mealRepository;

    public MealController(MealService mealService, UserService userService, MealRepository mealRepository) {
        this.mealService = mealService;
        this.userService = userService;
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

    @GetMapping({"/meals", "/meals/log"})
    public String logMealPage(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "date", required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              HttpSession session,
                              Model model) {
        User user = getActiveUser(session);
        List<Meal> meals = mealService.getMeals(user.getId());

        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            meals = meals.stream()
                    .filter(m -> (m.getFoodItem() != null && m.getFoodItem().toLowerCase().contains(q)) ||
                                 (m.getQuantity() != null && m.getQuantity().toLowerCase().contains(q)))
                    .toList();
        }

        if (date != null) {
            meals = meals.stream()
                    .filter(m -> date.equals(m.getDate()))
                    .toList();
        }

        Meal newMeal = new Meal();
        if (date != null) {
            newMeal.setDate(date);
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("meals", meals);
        model.addAttribute("newMeal", newMeal);
        model.addAttribute("search", search);
        model.addAttribute("selectedDate", date != null ? date : LocalDate.now());

        return "log-meal";
    }

    @PostMapping({"/meals", "/meals/log"})
    public String logMeal(@ModelAttribute("newMeal") Meal meal,
                          HttpServletRequest request,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = getActiveUser(session);
            mealService.logMeal(meal, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Meal logged successfully!");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/meals")) {
            return "redirect:/meals/log";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/meals/delete/{id}")
    public String deleteMeal(@PathVariable Long id,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        mealRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Meal entry removed.");
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/meals")) {
            return "redirect:/meals/log";
        }
        return "redirect:/dashboard";
    }
}
