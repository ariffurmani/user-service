package org.furmani.userservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 * Converts exceptions into appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle InvalidRequestException - typically caused by invalid input parameters.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequestException(InvalidRequestException ex) {
        logger.warn("Invalid request: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle UserAlreadyExistsException - thrown when attempting to create a duplicate user.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.warn("User already exists: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * Handle UserNotFoundException - thrown when a user is not found in the database.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle InvalidCredentialsException - thrown on invalid email/password during login.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.warn("Invalid credentials: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle InvalidTokenException - thrown when JWT token is invalid or expired.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException ex) {
        logger.warn("Invalid token: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle PasswordMismatchException - legacy exception for password validation.
     */
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordMismatchException(PasswordMismatchException ex) {
        logger.warn("Password mismatch: {}", ex.getMessage());
        Map<String, Object> body = buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle generic RuntimeException - catch-all for unexpected errors.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected runtime error", ex);
        Map<String, Object> body = buildErrorResponse(
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Build a standardized error response body.
     */
    private Map<String, Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}

