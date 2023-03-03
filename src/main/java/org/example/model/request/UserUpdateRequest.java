package org.example.model.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String avatar;
}
