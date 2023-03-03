package org.example.controller;

import org.example.config.JwtTokenUtil;
import org.example.model.request.JwtRequest;
import org.example.model.request.UserRegistrationRequest;
import org.example.model.request.UserUpdateRequest;
import org.example.model.response.JwtResponse;
import org.example.model.response.UserRespondDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list-user")
    public Map<String, Object> findAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "100") int size) {
        return userService.findAll(page, size);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
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

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserRegistrationRequest user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return ResponseEntity.status(400).body(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        if (userService.findByUsername(user.getUsername()) == null) {
            try {
                return ResponseEntity.ok(userService.save(user));
            } catch (SecurityException e) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
        } else {
            return ResponseEntity.status(400).body("There is already an account registered with that username");
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/update")
    public ResponseEntity<?> updateInformation(@RequestBody UserUpdateRequest userUpdateDto) {
        UserRespondDto userRespondDto = userService.updateUser(userUpdateDto);
        if (userRespondDto == null) {
            return ResponseEntity.status(400).body("Not found user");
        }
        return ResponseEntity.status(200).body(userRespondDto);
    }

}
