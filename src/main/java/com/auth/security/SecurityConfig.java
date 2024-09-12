package com.auth.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.auth.service.LoginAttemptService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    public SecurityConfig(UserDetailsService userDetailsService, LoginAttemptService loginAttemptService) {
        this.userDetailsService = userDetailsService;
        this.loginAttemptService = loginAttemptService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity (not recommended for production)
            .cors(cors -> cors.disable()) // Disable CORS as it's not necessary for JSP pages
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/api/**").permitAll()
                .requestMatchers("/login", "/register", "/home", "/logout", "/login.jsp", "/register.jsp", "/css/**", "/js/**").permitAll() // Allow access to these pages without authentication
                .anyRequest().authenticated() // All other pages require authentication
            )
            .formLogin(form -> form
                .loginPage("/login") // The login page URL
                .loginProcessingUrl("/login")  // Action URL for the login form (should match the form action in your JSP)
                .defaultSuccessUrl("/home.jsp", true)  // Redirect to home.jsp upon successful login
                .failureUrl("/login.jsp?error=true") // Redirect to login.jsp on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true") // Redirect to login page after logout
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            loginAttemptService.resetFailedAttempts(authentication.getName());
            response.sendRedirect("/home.jsp");
        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            loginAttemptService.loginFailed(request.getParameter("username"));
            response.sendRedirect("/login.jsp?error=true");
        };
    }
}
