package com.auth.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF if using stateless API
				.cors(cors -> cors.configurationSource(request -> {
					var corsConfig = new org.springframework.web.cors.CorsConfiguration();
					corsConfig.setAllowedOrigins(List.of("http://localhost:4200")); // Angular frontend URL
					corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
					corsConfig.setAllowedHeaders(List.of("*"));
					corsConfig.setAllowCredentials(true);
					return corsConfig;
				})).authorizeHttpRequests(auth -> auth.requestMatchers("/api/register", "/api/login","/api/unlock")
						.permitAll()
						.anyRequest().authenticated() 
				).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
				).httpBasic();

		return http.build();
	}
}
