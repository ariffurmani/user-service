package org.furmani.userservice.services;

import org.furmani.userservice.dtos.AuthenticatedUser;
import org.furmani.userservice.exceptions.*;
import org.furmani.userservice.models.User;

/**
 * Service interface for user authentication and token validation.
 * All methods throw unchecked exceptions for error handling.
 */
public interface UserService {

    /**
     * Registers a new user with provided credentials.
     *
     * @param name       the user's name (cannot be null or empty)
     * @param email      the user's email (cannot be null or empty, must be unique)
     * @param password   the user's password (cannot be null or empty)
     * @param role       the user's role (cannot be null or empty)
     * @return the created User object
     * @throws InvalidRequestException if any input parameter is null or empty
     * @throws UserAlreadyExistsException if a user with the given email already exists
     * @throws RuntimeException if an unexpected database error occurs
     */
    User signup(String name, String email, String password, String role);

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param username the user's email/username (cannot be null or empty)
     * @param password the user's password (cannot be null or empty)
     * @return a JWT token valid for 30 days
     * @throws InvalidRequestException if username or password is null or empty
     * @throws InvalidCredentialsException if the email is not found or password is incorrect
     * @throws RuntimeException if an unexpected error occurs during token generation
     * @throws PasswordMismatchException (legacy, superseded by InvalidCredentialsException)
     */
    String login(String username, String password) throws PasswordMismatchException;

    /**
     * Validates a JWT token and returns a lightweight authenticated user DTO.
     *
     * @param token the JWT token to validate (cannot be null or empty)
     * @return AuthenticatedUser containing the email and role names extracted from the token
     */
    AuthenticatedUser validateToken(String token) throws InvalidTokenException;
}
