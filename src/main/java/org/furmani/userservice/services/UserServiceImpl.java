package org.furmani.userservice.services;

import org.furmani.userservice.exceptions.InvalidCredentialsException;
import org.furmani.userservice.exceptions.InvalidTokenException;
import org.furmani.userservice.exceptions.PasswordMismatchException;
import org.furmani.userservice.models.Role;
import org.furmani.userservice.models.User;
import org.furmani.userservice.repositories.RoleRepository;
import org.furmani.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

import java.util.*;

@Service
public class UserServiceImpl implements  UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User signup(String name, String email, String password, String role) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return  existingUser.get();
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Find existing role or create and persist a new one atomically
        Role roleEntity = roleRepository.findByValue(role)
                .orElseGet(() -> roleRepository.save(new Role(role)));

        // Ensure the user's roles collection is initialized before adding
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(roleEntity);

        return userRepository.save(user);
    }

    @Override
    public String login(String username, String password) throws PasswordMismatchException {
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = calendar.getTime();

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles());
        claims.put("userId", user.getId());
        claims.put("exp", expiryDate);

        SecretKey key = Jwts.SIG.HS256.key().build();

        return Jwts.builder().claims(claims)
                .signWith(key)
                .compact();
    }

    @Override
    public User validateToken(String token) throws InvalidTokenException {
        return null;
    }
}
