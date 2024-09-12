package com.auth.model;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Assuming accounts never expire in your current setup
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Assuming credentials never expire in your current setup
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You might want to add logic to check if the account is enabled
        // For example, based on a `boolean` flag in the `User` entity
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // You can return authorities or roles associated with the user
        // For simplicity, returning null or an empty collection here
        return null;
    }

    // Getter for user, if needed
    public User getUser() {
        return user;
    }
}
