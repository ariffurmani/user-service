package org.furmani.userservice.services;

import org.furmani.userservice.exceptions.InvalidTokenException;
import org.furmani.userservice.exceptions.PasswordMismatchException;
import org.furmani.userservice.models.User;

public interface UserService {
    User signup(String name, String email, String password, String role);
    String login(String username, String password) throws PasswordMismatchException;
    User validateToken(String token) throws InvalidTokenException;
}
