package com.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.model.CustomUserDetails;
import com.auth.model.User;
import com.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the repository using the username
        User user = userRepository.findByUsername(username);

        // Throw exception if user is not found
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Return a CustomUserDetails object containing the user details
        return new CustomUserDetails(user);
    }
}
