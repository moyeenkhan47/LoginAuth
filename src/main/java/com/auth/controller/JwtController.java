package com.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.jwtsecurity.JwtHelper;
import com.auth.jwtsecurity.JwtRequest;
import com.auth.model.User;
import com.auth.service.CustomUserDetailsService;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor

@Slf4j
@Builder
@CrossOrigin("*")
@RequestMapping("/api")
public class JwtController {

    private final CustomUserDetailsService customService; // Ensure this service is injected
    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> login(@RequestBody JwtRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Perform authentication
            doAuthenticate(request.getUsername(), request.getPassword());

            // Fetch user details after successful authentication
            UserDetails userDetails = customService.loadUserByUsername(request.getUsername());

            if (userDetails == null) {
                response.put("success", "false");
                response.put("message", "User not found.");
                return ResponseEntity.badRequest().body(response);
            }

            // Assuming CustomUserDetailsService returns User object that can check lock status
            if (userDetails instanceof User && !((User) userDetails).isAccountNonLocked()) {
                response.put("success", "false");
                response.put("isLocked", true);
                response.put("message", "Account is locked.");
                return ResponseEntity.ok().body(response);
            }

            // Generate JWT token
            String token = jwtHelper.generateToken(userDetails);

            // Prepare success response
            response.put("success", "true");
            response.put("message", "Login successful!");
            response.put("jwtToken", token);

            // Return the token in the response header
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(response);

        } catch (BadCredentialsException e) {
            // Handle invalid credentials
            response.put("success", "false");
            response.put("isLocked", false);
            response.put("message", "Invalid credentials.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credentials Invalid!");
        }
    }
}
