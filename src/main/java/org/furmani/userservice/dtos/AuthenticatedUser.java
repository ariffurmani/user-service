package org.furmani.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.furmani.userservice.models.User;
import org.furmani.userservice.models.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AuthenticatedUser {
    private String email;
    private List<String> roles;

    public static AuthenticatedUser from(User user) {
        if (user == null) return null;
        AuthenticatedUser dto = new AuthenticatedUser();
        dto.setEmail(user.getEmail());
        List<String> roleNames = new ArrayList<>();
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                if (r != null && r.getValue() != null) roleNames.add(r.getValue());
            }
        }
        dto.setRoles(roleNames);
        return dto;
    }
}

