package org.furmani.userservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtException;
import org.furmani.userservice.exceptions.*;
import org.furmani.userservice.models.Role;
import org.furmani.userservice.models.User;
import org.furmani.userservice.repositories.RoleRepository;
import org.furmani.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

import java.util.*;

@Service
public class UserServiceImpl implements  UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    SecretKey secretKey;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,  SecretKey secretKey) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.secretKey = secretKey;
    }

    @Override
    public User signup(String name, String email, String password, String role) {
        logger.info("Attempting to signup user with email: {}", email);

        // Input validation
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Signup failed: email is null or empty");
            throw new InvalidRequestException("Email cannot be null or empty");
        }

        if (name == null || name.trim().isEmpty()) {
            logger.warn("Signup failed: name is null or empty");
            throw new InvalidRequestException("Name cannot be null or empty");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Signup failed: password is null or empty");
            throw new InvalidRequestException("Password cannot be null or empty");
        }

        if (role == null || role.trim().isEmpty()) {
            logger.warn("Signup failed: role is null or empty");
            throw new InvalidRequestException("Role cannot be null or empty");
        }

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email.trim());
        if (existingUser.isPresent()) {
            logger.warn("Signup failed: user with email {} already exists", email);
            throw new UserAlreadyExistsException("A user with email " + email + " already exists");
        }

        try {
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setPassword(passwordEncoder.encode(password));

            // Find existing role or create and persist a new one atomically
            Role roleEntity = roleRepository.findByValue(role.trim())
                    .orElseGet(() -> {
                        logger.info("Creating new role: {}", role);
                        return roleRepository.save(new Role(role.trim()));
                    });

            // Ensure the user's roles collection is initialized before adding
            if (user.getRoles() == null) {
                user.setRoles(new ArrayList<>());
            }
            user.getRoles().add(roleEntity);

            User savedUser = userRepository.save(user);
            logger.info("User successfully signed up with email: {}", email);
            return savedUser;
        } catch (Exception e) {
            logger.error("An error occurred during user signup with email: {}", email, e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public String login(String username, String password) throws PasswordMismatchException {
        logger.info("Attempting to login user with email: {}", username);

        // Input validation
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Login failed: username is null or empty");
            throw new InvalidRequestException("Email/username cannot be null or empty");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Login failed: password is null or empty");
            throw new InvalidRequestException("Password cannot be null or empty");
        }

        try {
            Optional<User> userOpt = userRepository.findByEmail(username.trim());
            if (userOpt.isEmpty()) {
                logger.warn("Login failed: user with email {} not found", username);
                throw new InvalidCredentialsException("Invalid email or password");
            }

            User user = userOpt.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("Login failed: invalid password for user {}", username);
                throw new InvalidCredentialsException("Invalid email or password");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Date expiryDate = calendar.getTime();

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("roles", user.getRoles());
            claims.put("userId", user.getId());
            claims.put("exp", expiryDate.getTime() / 1000); // JWT exp is in seconds

            String token = Jwts.builder()
                    .claims(claims)
                    .signWith(secretKey)
                    .compact();

            logger.info("User {} successfully logged in", username);
            return token;
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred during login for user: {}", username, e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public User validateToken(String token) throws InvalidTokenException {
        logger.info("Validating token");

        // Input validation
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Token validation failed: token is null or empty");
            throw new InvalidTokenException("Token cannot be null or empty");
        }

        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
            Claims claims = jwtParser.parseSignedClaims(token.trim()).getPayload();

            // Validate expiry claim exists
            Object expObject = claims.get("exp");
            if (expObject == null) {
                logger.warn("Token validation failed: exp claim is missing");
                throw new InvalidTokenException("Token is missing expiry claim");
            }

            Long expiryDate;
            try {
                expiryDate = ((Number) expObject).longValue() * 1000; // Convert from seconds to milliseconds
            } catch (ClassCastException e) {
                logger.warn("Token validation failed: exp claim is not a valid number");
                throw new InvalidTokenException("Token has invalid expiry format");
            }

            long currentDate = System.currentTimeMillis();

            // Check if token has expired
            if (expiryDate <= currentDate) {
                logger.warn("Token validation failed: token has expired. Expiry: {}, Current: {}", expiryDate, currentDate);
                throw new InvalidTokenException("Token has expired");
            }

            // Extract email from claims
            String email = (String) claims.get("email");
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Token validation failed: email claim is missing");
                throw new InvalidTokenException("Token is missing email claim");
            }

            // Fetch user from repository
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Token validation failed: user with email {} not found in database", email);
                throw new UserNotFoundException("User associated with token not found");
            }

            logger.info("Token validation successful for user: {}", email);
            return userOpt.get();
        } catch (InvalidTokenException | UserNotFoundException e) {
            throw e;
        } catch (JwtException e) {
            logger.warn("Token validation failed: JWT parsing error", e);
            throw new InvalidTokenException("Invalid token: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during token validation", e);
            throw new RuntimeException("Token validation failed: " + e.getMessage(), e);
        }
    }
}
