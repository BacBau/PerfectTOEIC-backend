package org.example.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Entity
public class User extends AbstractEntity {

    private String username;

    private String password;

    private String email;

    private boolean emailVerified;

    private String fullName;

    private String role;

    private boolean active;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public enum Provider {
        LOCAL, GOOGLE, FACEBOOK
    }
}
