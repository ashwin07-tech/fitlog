# FitLog — Personal Workout and Calorie Tracker (MVP)

A reliable, lightweight Spring Boot backend service implementing the Core MVP Deliverables based on the FitLog Use Case and Class Diagrams.

---

## 🎯 Core MVP Deliverables & Endpoints

| Deliverable | HTTP Method & Path | Description |
| :--- | :--- | :--- |
| **Workout Tracking** | `POST /api/workouts`<br>`GET /api/workouts` | Log workouts with `type`, `duration` (minutes), and `caloriesBurnt`. |
| **Meal Tracking** | `POST /api/meals`<br>`GET /api/meals` | Log meals with `foodItem`, `quantity`, and `calories` consumed. |
| **Daily Balance** | `GET /api/summary/daily?date=YYYY-MM-DD` | Returns total calories burned vs. calories consumed and net balance. |
| **Weekly Metrics (Goal)** | `POST /api/goals`<br>`GET /api/goals` | Set and view weekly workout-count targets and calorie objectives. |
| **Weekly Metrics (Trend)**| `GET /api/summary/weekly?date=YYYY-MM-DD` | Retrieve weekly trend summaries (**conditioned on at least 1 entry for that week**). |
| **Validation & Integrity**| Global `@ControllerAdvice` | Service-layer validation rejecting negative numbers for calories, returning structured errors instead of stack traces. |

*(A unified, responsive single-page web dashboard is also provided at `http://localhost:8080/`)*

---

## 📐 Architecture & Entities

- **`User`**: `id`, `username`, `email`, `password` (supports registration and login)
- **`Workout`**: `id`, `type`, `duration` (min), `caloriesBurnt` ($\ge 0$), `date`, `user`
- **`Meal`**: `id`, `foodItem`, `quantity`, `calories` ($\ge 0$), `date`, `user`
- **`Goal`**: `id`, `weekStartDate`, `targetWorkoutCount` ($\ge 1$), `targetCalories`, `user`
- **`DailySummaryDto`**: `date`, `totalCaloriesBurnt`, `totalCaloriesConsumed`, `netCalories`, `workouts`, `meals`
- **`WeeklySummaryDto`**: `weekStartDate`, `weekEndDate`, `totalWorkoutsCompleted`, `targetWorkouts`, `goalMet`, `totalCaloriesBurnt`, `totalCaloriesConsumed`

---

## 🛡️ Business Rules Enforced

1. **Non-Negative Calories:**
   - Workouts with `caloriesBurnt < 0` or Meals with `calories < 0` are rejected in the service layer with a `BusinessRuleException` (HTTP 400).
2. **Positive Duration:**
   - Workouts with `duration <= 0` minutes are rejected with a `BusinessRuleException` (HTTP 400).
3. **Weekly Summary Condition:**
   - A weekly summary is **only generated once at least one entry (workout or meal) exists for that week**. If none exist, a `NoDataFoundException` (HTTP 404) is thrown and formatted gracefully by `@ControllerAdvice`.

---

## 🚀 Running in IntelliJ IDEA & Accessing FitLog

1. In IntelliJ IDEA, click **Reload All Maven Projects** (🔄 in the Maven tool window on the right).
2. Run [`FitLogApplication.java`](src/main/java/com/fitlog/FitLogApplication.java).
3. **Web Interfaces:**
   - 🏠 **Main Dashboard:** `http://localhost:8080/` (or `http://localhost:8080/dashboard`)
   - 🏃 **Workout Tracking:** `http://localhost:8080/workouts/log`
   - 🥗 **Meal Tracking:** `http://localhost:8080/meals/log`
   - 👤 **Profile & BMR Calculator:** `http://localhost:8080/profile`
4. **API Documentation & Testing:**
   - 📘 **Interactive Swagger UI:** `http://localhost:8080/swagger-ui.html`
   - Test REST endpoints directly using browser, curl, or Postman at `http://localhost:8080/api/...`.
5. **Assessment Presentation Guide:**
   - See [`PROJECT_DOCUMENTATION.md`](PROJECT_DOCUMENTATION.md) for full viva Q&A and rubric breakdown.
