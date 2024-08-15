package com.team2.finalproject.global.security.details;

import com.team2.finalproject.domain.users.model.entity.Users;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
public class UserDetailsImpl implements UserDetails {
    private final Users users;

    public UserDetailsImpl(Users users) {
        if (users == null) {
            throw new IllegalArgumentException("Users 값이 null이 될 수 없습니다.");
        }
        this.users = users;
    }

    public long getId() {
        return users.getId();
    }

    public String getName() {
        return users.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}