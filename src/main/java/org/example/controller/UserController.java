package org.example.controller;

import org.example.auth.AuthoritiesConstants;
import org.example.config.JwtTokenUtil;
import org.example.exception.EnglishExamException;
import org.example.model.error.ErrorCode;
import org.example.model.request.JwtRequest;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.request.UserUpdateRequest;
import org.example.model.response.JwtResponse;
import org.example.model.response.UserRespondDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @GetMapping("/list-user")
    public Map<String, Object> findAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "100") int size) {
        return userService.findAll(page, size);
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @PostMapping("/inactive")
    public ResponseEntity<HttpStatus> banningUser(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null) throw new EnglishExamException(ErrorCode.BAD_REQUEST);
        userService.banningUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @PostMapping("/active")
    public ResponseEntity<HttpStatus> unbanningUser(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null) throw new EnglishExamException(ErrorCode.BAD_REQUEST);
        userService.unbanningUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @Secured(AuthoritiesConstants.ROLE_USER)
    public UserRespondDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteById(@PathVariable String id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok(200);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Not found id user");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (Exception e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                return ResponseEntity.badRequest().body(new HashMap<>(Map.of("message", "Thông tin đăng nhập không hợp lệ!")));
            }
            if (e.getMessage().equals("USER_DISABLED")) {
                return ResponseEntity.status(403).body(new HashMap<>(Map.of("message", "USER bị khoá hoặc không có quyền truy cập!")));
            }
            return ResponseEntity.status(500).body(new HashMap<>(Map.of("message", "Máy chủ gặp vấn đề, vui lòng thử lại sau!!")));
        }

        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserRegistrationRequest user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return ResponseEntity.status(400).body(new HashMap<>(Map.of("message", result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage))));
        }
        if (userService.findByUsername(user.getUsername()) == null) {
            try {
                return ResponseEntity.ok(userService.save(user));
            } catch (SecurityException e) {
                return ResponseEntity.status(401).body(new HashMap<>(Map.of("message", "Không được phép!")));
            }
        } else {
            return ResponseEntity.status(400).body(new HashMap<>(Map.of("message", "Tài khoản đã được đăng ký!")));
        }

    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @Secured(AuthoritiesConstants.ROLE_USER)
    @PostMapping("/update")
    public ResponseEntity<?> updateInformation(@RequestBody UserUpdateRequest userUpdateDto) {
        UserRespondDto userRespondDto = userService.updateUser(userUpdateDto);
        if (userRespondDto == null) {
            return ResponseEntity.status(400).body("Not found user");
        }
        return ResponseEntity.status(200).body(userRespondDto);
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @PostMapping("/permission")
    public ResponseEntity<HttpStatus> setAdminToUser(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null) throw new EnglishExamException(ErrorCode.BAD_REQUEST);
        userService.setAdmin(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
