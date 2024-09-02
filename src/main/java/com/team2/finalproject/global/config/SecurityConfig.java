package com.team2.finalproject.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.finalproject.global.security.details.UserDetailsServiceImpl;
import com.team2.finalproject.global.security.exception.JwtAuthenticationEntryPoint;
import com.team2.finalproject.global.security.filter.JwtAuthenticationFilter;
import com.team2.finalproject.global.security.filter.LoginAuthenticationFilter;
import com.team2.finalproject.global.security.jwt.JwtProvider;
import com.team2.finalproject.global.security.jwt.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final ObjectMapper objectMapper;
    private final CorsConfig corsConfig;

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return new LoginAuthenticationFilter(
                "/api/users/login",
                authenticationConfiguration.getAuthenticationManager(),
                objectMapper,
                jwtProvider,
                tokenService
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "^(?!/api/).*");
    }

    private final String[] swagger = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(swagger).permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/dispatch/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/dispatch-number/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/transport-order/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/delivery-destination/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/dispatch-detail/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/center/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/center/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/center/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/users/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(loginAuthenticationFilter(authenticationConfiguration), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, tokenService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"message\": \"Access Denied\"}");
                        })));

        return http.build();
    }
}
