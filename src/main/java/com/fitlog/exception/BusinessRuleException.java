package com.fitlog.exception;

/**
 * Exception thrown when a core fitness tracking business rule is violated.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
