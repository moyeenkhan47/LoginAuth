package com.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.model.User;
import com.auth.repository.UserRepository;

@Service
public class LoginAttemptService {

    private final int MAX_FAILED_ATTEMPTS = 3;
    private final Duration LOCK_TIME_DURATION = Duration.ofMinutes(15); // 15 minutes

    @Autowired
    private UserRepository userRepository;

    public void loginFailed(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
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

    public void resetFailedAttempts(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getFailedAttempts() > 0) {
            user.setFailedAttempts(0);
            userRepository.save(user);
        }
    }

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
