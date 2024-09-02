package com.team2.finalproject.global.security.authentication;

import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Users users;
        try {
            users = usersRepository.findByUsernameOrThrow(username);
        } catch (UsersException e) {
            throw new UsernameNotFoundException("존재하는 아이디가 없습니다.");
        }

        if(!passwordEncoder.matches(password, users.getEncryptedPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(users);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 지원하는 인증 유형
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
