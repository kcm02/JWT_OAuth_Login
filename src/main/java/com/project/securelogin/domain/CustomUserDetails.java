package com.project.securelogin.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public int getFailedLoginAttempts() {
        return user.getFailedLoginAttempts();
    }

    public void incrementFailedLoginAttempts() {
        user.incrementFailedLoginAttempts();
    }

    public void resetFailedLoginAttempts() {
        user.resetFailedLoginAttempts();
    }

    public void lockAccount() {
        user.lockAccount();
    }

    public void unlockAccount() {
        user.unlockAccount();
    }

    public boolean isLockTimeExpired(int lockDurationMinutes) {
        return user.isLockTimeExpired(lockDurationMinutes);
    }
}
