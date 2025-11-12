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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // keep CSRF disabled for APIs (you already disabled it previously)
                .csrf(AbstractHttpConfigurer::disable)

                // stateless session management for JWT-based APIs
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Allow unauthenticated POSTs to the stripe webhook endpoint
                .authorizeHttpRequests(auth -> auth
                        // public application endpoints
                        .requestMatchers(publicRoutes).permitAll()
                        // allow Stripe webhooks (server-to-server) without auth
                        .requestMatchers("/api/v1/webhook/**").permitAll()
                        .requestMatchers("/webhook/**").permitAll()
                        // role-based routes
                        .requestMatchers("/admin/**").hasAnyRole("HOTEL_MANAGER", "ADMIN")
                        .requestMatchers("/booking/**").hasAnyRole("GUEST", "ADMIN")
                        // everything else requires authentication
                        .anyRequest().authenticated()
                );

        // add JWT filter after we've declared the permitAll for webhook;
        // the filter should itself skip webhook URIs (see note below)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncode() {
        return new BCryptPasswordEncoder();
    }
}