package com.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.auth.jwtsecurity.JwtAuthenticationEntryPoint;
import com.auth.jwtsecurity.JwtAuthenticationFilter;
import com.auth.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint point; // Automatically injected with RequiredArgsConstructor
    private final JwtAuthenticationFilter filter; // Automatically injected with RequiredArgsConstructor
    private final CustomUserDetailsService userDetailsService; // Automatically injected with RequiredArgsConstructor

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs (stateless)
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:4200")); // Frontend URL
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // HTTP methods
                corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Allow specific headers
                corsConfig.setExposedHeaders(List.of("Authorization")); // Expose Authorization header in response
                corsConfig.setAllowCredentials(true); // Allow credentials like cookies or Authorization header
                return corsConfig;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( "/api/authenticate","/api/register","/api/unlock").permitAll() // Permit these routes without authentication
                .anyRequest().authenticated()) // Other requests require authentication
            .exceptionHandling(ex -> ex.authenticationEntryPoint(point)) // Custom entry point for unauthorized access
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Stateless session

        // Add custom JWT authentication filter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
