package org.furmani.userservice.services;

import org.furmani.userservice.exceptions.InvalidTokenException;
import org.furmani.userservice.exceptions.PasswordMismatchException;
import org.furmani.userservice.models.Role;
import org.furmani.userservice.models.User;
import org.furmani.userservice.repositories.RoleRepository;
import org.furmani.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;

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
        return null;
    }

    @Override
    public User validateToken(String token) throws InvalidTokenException {
        return null;
    }
}
