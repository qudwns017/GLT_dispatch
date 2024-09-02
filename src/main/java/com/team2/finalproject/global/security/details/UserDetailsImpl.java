package com.team2.finalproject.global.security.details;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.entity.Users;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@ToString
public class UserDetailsImpl implements UserDetails {
    private final Users users;

    public UserDetailsImpl(Users users) {
        if (users == null) {
            throw new UsersException(UsersErrorCode.NOT_FOUND_USER);
        }
        this.users = users;
    }

    public long getId() {
        return users.getId();
    }

    public String getName() {
        return users.getName();
    }

    public Center getCenter() {
        return users.getCenter();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()));
    }

    @Override
    public String getPassword() {
        return users.getEncryptedPassword();
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