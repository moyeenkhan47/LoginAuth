package com.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.model.User;
import com.auth.repository.UserRepository;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration LOCK_TIME_DURATION = Duration.ofMinutes(15); // 15 minutes

    @Autowired
    private UserRepository userRepository;

    // Method to handle failed login attempts
    public void loginFailed(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int newFailedAttempts = user.getFailedAttempts() + 1;

            if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
            } else {
                user.setFailedAttempts(newFailedAttempts);
            }

            userRepository.save(user);
        }
    }

    // Method to reset failed login attempts
    public void resetFailedAttempts(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getFailedAttempts() > 0) {
                user.setFailedAttempts(0);
                userRepository.save(user);
            }
        }
    }

    // Method to unlock a user when lock time has expired
    public boolean unlockWhenTimeExpired(User user) {
        if (user.getLockTime() == null) return false;

        LocalDateTime unlockTime = user.getLockTime().plus(LOCK_TIME_DURATION);
        if (LocalDateTime.now().isAfter(unlockTime)) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempts(0);
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
