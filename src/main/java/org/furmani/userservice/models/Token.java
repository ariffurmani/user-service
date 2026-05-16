package org.furmani.userservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Token extends BaseEntity {
    private String value;
    private Date expirationDate;
    

}
