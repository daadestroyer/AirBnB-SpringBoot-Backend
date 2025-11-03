package com.org.AirBnB.config;

import com.org.AirBnB.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final static String[] publicRoutes = {
            "/public/**",
            "/auth/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/oauth2/authorization/**",
            "/home",
            "/hotels/**"
    };

    // WORKING WITH DIFFERENT ROLES
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // if you store JWT in cookies, re-evaluate CSRF

                // allow session only when needed (OAuth2 needs it)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicRoutes).permitAll()
                        .requestMatchers( "/admin/**").hasAnyRole("HOTEL_MANAGER","ADMIN")
                        .requestMatchers("/booking/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncode(){
        return new BCryptPasswordEncoder();
    }
}
