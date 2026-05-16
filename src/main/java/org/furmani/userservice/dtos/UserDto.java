package org.furmani.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.furmani.userservice.models.Role;
import org.furmani.userservice.models.User;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private List<Role> roles;

    public static UserDto  from(User user){
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());

        return userDto;
    }
}
