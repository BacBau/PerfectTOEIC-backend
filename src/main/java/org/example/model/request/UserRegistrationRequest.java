package org.example.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class UserRegistrationRequest {

    @NotBlank(message = "username must not be blank")
    private String username;

    @NotBlank(message = "password must not be blank")
    private String password;

    @NotBlank(message = "fullName must not be blank")
    private String fullName;

}
