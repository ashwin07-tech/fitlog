package com.fitlog.config;

import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * Initializes clean seed data for the FitLog MVP dashboard.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final GoalRepository goalRepository;

    public DataInitializer(UserRepository userRepository,
                           WorkoutRepository workoutRepository,
                           MealRepository mealRepository,
                           GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            logger.info("Initializing FitLog MVP sample data...");

            // 1. Seed Users (demonstrating User entity count & profiles)
            User user1 = new User("alex_runner", "alex.runner@example.com", "Alex Mercer", 72.5, LocalDate.of(1998, 5, 14));
            user1 = userRepository.save(user1);

            User user2 = new User("sarah_fit", "sarah.fitness@example.com", "Sarah Jenkins", 58.0, LocalDate.of(2001, 8, 22));
            userRepository.save(user2);

            User user3 = new User("mike_lift", "mike.lift@example.com", "Mike Davis", 84.0, LocalDate.of(1995, 3, 10));
            userRepository.save(user3);

            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            // 2. Seed Sample Workouts with Notes (Cardio & Strength)
            workoutRepository.save(new Workout(user1, "Cardio - Morning Run", 35, 320, monday, "Outdoor trail run, steady pace"));
            workoutRepository.save(new Workout(user1, "Strength - Upper Body", 45, 280, monday.plusDays(1), "Chest, shoulders and triceps"));
            workoutRepository.save(new Workout(user1, "Cardio - HIIT Cycling", 30, 350, today, "High intensity interval workout on stationary bike"));

            // 3. Seed Sample Meals
            mealRepository.save(new Meal(user1, "Oatmeal with Blueberries", "1 bowl (300g)", 380, today));
            mealRepository.save(new Meal(user1, "Grilled Chicken Rice Bowl", "1 plate (400g)", 620, today));
            mealRepository.save(new Meal(user1, "Greek Yogurt & Almonds", "1 cup (200g)", 190, today));
            mealRepository.save(new Meal(user1, "Fresh Apple", "1 medium", 95, today));

            // 4. Seed Weekly Goal (4 workouts, 2000 kcal)
            Goal goal1 = new Goal(user1, monday, 4, 2000);
            goal1.setGoalType("WEEKLY_WORKOUTS");
            goal1.setTargetValue(4.0);
            goalRepository.save(goal1);

            logger.info("FitLog MVP seed data successfully loaded with users, workouts, meals, and goals.");
        }
    }
}
