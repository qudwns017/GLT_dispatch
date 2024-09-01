package com.team2.finalproject.global.security.authentication;

import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Users users = usersRepository.findByUsernameOrThrow(username);

        if(!passwordEncoder.matches(password, users.getEncryptedPassword())) {
            throw new UsersException(UsersErrorCode.PASSWORD_MISMATCH);
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
