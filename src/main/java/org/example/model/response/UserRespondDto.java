package org.example.model.response;

import lombok.Data;

@Data
public class UserRespondDto {
    private String id;

    private String username;

    private String email;

    private String role;

    private boolean emailVerified;

    private String fullName;

    private boolean active;

    private String avatar;
}
