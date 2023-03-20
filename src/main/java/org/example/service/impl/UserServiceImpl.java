package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.auth.AuthoritiesConstants;
import org.example.config.Constants;
import org.example.exception.BannedUserException;
import org.example.exception.EnglishExamException;
import org.example.minio.MinioAdapter;
import org.example.model.entity.User;
import org.example.model.error.ErrorCode;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.request.UserUpdateRequest;
import org.example.model.response.UserRespondDto;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Value("${app.admin.username}")
    private String usernameAdminApp;
    @Value("${app.admin.password}")
    private String passwordAdminApp;

    @PostConstruct
    private void initAdmin() {
        if (userRepository.findByUsername(usernameAdminApp).isPresent()) return;
        User user = new User();
        user.setPassword(passwordEncoder.encode(this.passwordAdminApp));
        user.setUsername(this.usernameAdminApp);
        user.setFullName("ADMIN-APP");
        user.setRole(AuthoritiesConstants.ROLE_USER + "," + AuthoritiesConstants.ROLE_ADMIN);
        user.setProvider(User.Provider.LOCAL);
        user.setActive(true);
        user.setCreatedBy("APP");
        userRepository.save(user);
        log.info("Init admin done!!");
    }

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

    public void setAdmin(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EnglishExamException(ErrorCode.USER_NOT_FOUND));
        user.setRole(AuthoritiesConstants.ROLE_USER + "," + AuthoritiesConstants.ROLE_ADMIN);
        userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        String roles = user.get().getRole();
        if (!user.get().isActive()) {
            throw new BannedUserException("Banded perform action!");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword() != null ? user.get().getPassword() : "",
                getAuthorities(roles));
    }

    private List<SimpleGrantedAuthority> getAuthorities(String roles) {
        String[] rolesString = roles.split(",");
        List<SimpleGrantedAuthority> result =  new ArrayList<>();
        for (int i = 0; i < rolesString.length; i ++) {
            result.add(new SimpleGrantedAuthority(rolesString[i]));
        }
        return result;
    }


    public Map<String, Object> findAll(int page, int size) {
        if (page < 1 || size < 1) return null;
        Pageable paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdDate"));
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

    public void banningUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EnglishExamException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
    }

    public void unbanningUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EnglishExamException(ErrorCode.USER_NOT_FOUND));
        user.setActive(true);
        userRepository.save(user);
    }
}
