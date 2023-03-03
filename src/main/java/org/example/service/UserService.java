package org.example.service;


import org.example.model.entity.User;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.request.UserUpdateRequest;
import org.example.model.response.UserRespondDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    UserRespondDto save(UserRegistrationRequest registration);

    Map<String, Object> findAll(int page, int size);

    UserRespondDto getCurrentUser();

    UserRespondDto findByUsername(String username);

    void deleteById(String id);

    UserRespondDto updateUser(UserUpdateRequest userUpdateDto);

    List<UserRespondDto> searchUser(String key);

    void processOAuthPostLogin(String username, String name, String avatar, User.Provider provider);

}
