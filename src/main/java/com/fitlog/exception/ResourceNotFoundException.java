package com.fitlog.exception;

/**
 * Exception thrown when a requested resource (User, Workout, Meal, Goal) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
