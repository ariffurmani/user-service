package org.furmani.userservice.controllers;

import org.furmani.userservice.dtos.LoginRequestDto;
import org.furmani.userservice.dtos.SignupRequestDto;
import org.furmani.userservice.dtos.UserDto;
import org.furmani.userservice.dtos.AuthenticatedUser;
import org.furmani.userservice.exceptions.*;
import org.furmani.userservice.models.User;
import org.furmani.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user authentication and management endpoints.
 * All exceptions are handled globally by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user with the provided credentials.
     *
     * @param signupRequest contains name, email, password, and role
     * @return the created user as a UserDto with HTTP 201 Created status
     * @throws InvalidRequestException if any required field is missing or empty
     * @throws UserAlreadyExistsException if a user with the email already exists
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto signupRequest) {
        User user = userService.signup(
                signupRequest.getName(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.from(user));
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param loginRequest contains email and password
     * @return JWT token as response body with HTTP 200 OK status
     * @throws InvalidRequestException if email or password is missing
     * @throws InvalidCredentialsException if email not found or password is incorrect
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) {
        String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    /**
     * Validate a JWT token and return basic authenticated user info (email + role names).
     *
     * @param token the JWT token as a query parameter
     * @return the authenticated user as an AuthenticatedUser DTO with HTTP 200 OK status
     * @throws InvalidRequestException if token is missing or empty
     * @throws InvalidTokenException if token is invalid, malformed, or expired
     */
    @GetMapping("/validateToken")
    public ResponseEntity<AuthenticatedUser> validateToken(@RequestParam String token) {
        AuthenticatedUser authUser = userService.validateToken(token);
        return ResponseEntity.ok(authUser);
    }
}
