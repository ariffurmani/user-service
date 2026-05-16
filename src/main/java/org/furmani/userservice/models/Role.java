package org.furmani.userservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends BaseEntity {
    private String value;

    public Role(String role) {
        value = role;
    }

    public Role() {

    }
}
