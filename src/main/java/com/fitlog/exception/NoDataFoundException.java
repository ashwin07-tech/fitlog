package com.fitlog.exception;

/**
 * Exception thrown when business logic requires existing historical data
 * (e.g. at least one entry must exist in a week before a weekly summary can be generated).
 */
public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}
