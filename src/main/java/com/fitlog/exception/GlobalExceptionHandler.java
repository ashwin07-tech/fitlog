package com.fitlog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler capturing application exceptions and returning
 * structured error responses (JSON for REST API, HTML for Web).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/")) || (accept != null && accept.contains("application/json"));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Object handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request, Model model) {
        logger.warn("Business rule violation: {}", ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Bad Request");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Validation / Business Rule Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("suggestion", "Please review the requirements (e.g. non-negative calories, positive duration) and retry.");
        return "error/error";
    }

    @ExceptionHandler(NoDataFoundException.class)
    public Object handleNoDataFoundException(NoDataFoundException ex, HttpServletRequest request, Model model) {
        logger.info("Summary requested with no existing records: {}", ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("title", "No Data Found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("suggestion", "Log at least one workout or meal for this week to unlock your weekly summary.");
        return "error/error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        logger.warn("Resource not found: {}", ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("title", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request, Model model) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String combined = String.join(", ", errorMessages);

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Validation Failed");
            body.put("message", combined);
            body.put("errors", errorMessages);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Validation Failed");
        model.addAttribute("message", combined);
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        logger.error("Internal Server Error: ", ex);

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "Internal Server Error");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("title", "Server Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        return "error/error";
    }
}
