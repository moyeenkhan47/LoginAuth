package com.auth.contoller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.auth.dto.UserRegistrationDto;
import com.auth.model.User;
import com.auth.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Handle user registration via JSON (RESTful API)
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        Map<String, Object> response = new HashMap<>();

        // Validate that passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            response.put("error", "Passwords do not match.");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the username already exists
        Optional<User> existingUser = userService.findUserByUsername(registrationDto.getUsername());
        if (existingUser.isPresent()) {
            response.put("error", "Username already exists.");
            return ResponseEntity.badRequest().body(response);
        }

        // Create and save a new user with an encoded password
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword())); // Encode the password
        newUser.setAccountNonLocked(true);
        newUser.setFailedAttempts(0);
        newUser.setLockTime(null);

        userService.saveUser(newUser);

        response.put("message", "User registered successfully.");
        return ResponseEntity.ok(response);
    }

    // Handle user login via JSON (RESTful API)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userService.findUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!user.isAccountNonLocked()) {
                response.put("error", "Account is locked.");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify the password
            if (passwordEncoder.matches(password, user.getPassword())) {
                userService.resetFailedAttempts(user);
                response.put("message", "Login successful!");
                return ResponseEntity.ok(response);
            } else {
                userService.increaseFailedAttempts(user);
                response.put("error", "Invalid password.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Unlock the account via JSON (RESTful API)
    @PostMapping("/unlock")
    public ResponseEntity<Map<String, Object>> unlockAccount(@RequestBody Map<String, String> unlockRequest) {
        String username = unlockRequest.get("username");
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userService.findUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the account can be unlocked (after 15 minutes)
            if (user.getLockTime() != null && user.getLockTime().isBefore(LocalDateTime.now().minusMinutes(15))) {
                userService.unlockAccount(user);
                response.put("message", "Account unlocked.");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Account cannot be unlocked yet.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
