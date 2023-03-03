package org.example.service.mapper;

import org.example.config.Constants;
import org.example.minio.MinioAdapter;
import org.example.model.entity.User;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.response.UserRespondDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MinioAdapter minioAdapter;


    public UserRespondDto userToUserRespondDto(User user) {
        UserRespondDto userRespondDto = new UserRespondDto();

        userRespondDto.setEmail(user.getEmail());
        userRespondDto.setUsername(user.getUsername());
        userRespondDto.setActive(user.isActive());
        userRespondDto.setEmailVerified(user.isEmailVerified());
        userRespondDto.setId(user.getId());
        userRespondDto.setRole(user.getRole());
        userRespondDto.setFullName(user.getFullName());
        userRespondDto.setAvatar(user.getAvatar() == null ? "https://static.thenounproject.com/png/5034901-200.png" : user.getAvatar());
        return userRespondDto;
    }

    public User userRegistrationDtoToUser(UserRegistrationRequest userRegistrationDto) {
        String username = Constants.getCurrentUser();
        User user = new User();
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setUsername(userRegistrationDto.getUsername());
        user.setFullName(userRegistrationDto.getFullName());
        user.setRole(Constants.ROLE_USER);
        user.setProvider(User.Provider.LOCAL);
        user.setActive(true);
        user.setCreatedBy(username);
        return user;
    }

    public List<UserRespondDto> usersToUserRespondDtos(List<User> users) {
        return users.stream().map(this::userToUserRespondDto).collect(Collectors.toList());
    }
}
