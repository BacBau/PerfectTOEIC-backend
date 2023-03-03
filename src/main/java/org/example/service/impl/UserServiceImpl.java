package org.example.service.impl;

import org.example.config.Constants;
import org.example.minio.MinioAdapter;
import org.example.model.entity.User;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.request.UserUpdateRequest;
import org.example.model.response.UserRespondDto;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MinioAdapter minioAdapter;

    public UserRespondDto save(UserRegistrationRequest registration) {
        User user = userRepository.save(userMapper.userRegistrationDtoToUser(registration));
        return userMapper.userToUserRespondDto(user);
    }

    public void processOAuthPostLogin(String username, String name, String avatar, User.Provider provider) {
        Optional<User> existUser = userRepository.findByUsername(username);

        if (existUser.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setProvider(provider);
            user.setRole(Constants.ROLE_USER);
            user.setActive(true);
            user.setAvatar(avatar);
            user.setCreatedBy(username);
            user.setFullName(name);
            userRepository.save(user);
        }

    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword() != null ? user.get().getPassword() : "",
                Arrays.asList(new SimpleGrantedAuthority(user.get().getRole())));
    }


    public Map<String, Object> findAll(int page, int size) {
        if (page < 1 || size < 1) return null;
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<User> pageTuts = userRepository.findAll(paging);
        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", pageTuts.getNumber() + 1);
        response.put("totalItems", pageTuts.getTotalElements());
        response.put("totalPages", pageTuts.getTotalPages());
        response.put("listUser", userMapper.usersToUserRespondDtos(pageTuts.getContent()));
        return response;
    }

    @Override
    public UserRespondDto getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Optional<User> user = userRepository.findByUsername(((UserDetails) principal).getUsername());
            if (user.isPresent()) return userMapper.userToUserRespondDto(user.get());
        }
        return null;
    }

    @Override
    public UserRespondDto findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) return userMapper.userToUserRespondDto(user.get());
        return null;
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserRespondDto updateUser(UserUpdateRequest userUpdateDto) {
        Optional<User> user = userRepository.findByUsername(Constants.getCurrentUser());
        if (!user.isPresent()) {
            return null;
        }
        if (userUpdateDto.getAvatar() != null) {
            user.get().setAvatar(userUpdateDto.getAvatar());
        }
        if (userUpdateDto.getFullName() != null) {
            user.get().setFullName(userUpdateDto.getFullName());
        }
        return userMapper.userToUserRespondDto(userRepository.save(user.get()));
    }

    @Override
    public List<UserRespondDto> searchUser(String key) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(key, key);
        for (User user : users) {
            if (user.getUsername().equals(Constants.getCurrentUser())) {
                users.remove(user);
                break;
            }
        }
        ;
        return userMapper.usersToUserRespondDtos(users);
    }

}
