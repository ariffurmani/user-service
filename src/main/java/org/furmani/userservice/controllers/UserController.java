package org.furmani.userservice.controllers;

import org.furmani.userservice.dtos.LoginRequestDto;
import org.furmani.userservice.dtos.SignupRequestDto;
import org.furmani.userservice.dtos.UserDto;
import org.furmani.userservice.exceptions.InvalidTokenException;
import org.furmani.userservice.exceptions.PasswordMismatchException;
import org.furmani.userservice.models.User;
import org.furmani.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignupRequestDto signupRequest) {
        User user = userService.signup(signupRequest.getName(), signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getRole());
        return UserDto.from(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequest) throws PasswordMismatchException {
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @GetMapping("/validateToken")
    public UserDto validateToken(@RequestParam String token) throws InvalidTokenException {
        User user = userService.validateToken(token);
        return UserDto.from(user);
    }
}
