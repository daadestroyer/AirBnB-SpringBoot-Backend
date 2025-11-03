package com.org.AirBnB.security;

import com.org.AirBnB.entities.User;
import com.org.AirBnB.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT Filter loaded");
        // extract the token
        String token = extractTokenFromBearerToken(request);

        // If no token found, continue a filter chain (no authentication set)
        if (token == null) {
            filterChain.doFilter(request, response);
            return; // IMPORTANT: return after delegating to chain
        }

        try {
            // validate token (signature + expiration)
            if (!jwtService.isTokenValid(token)) {
                log.info("JWT Token is not valid");
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.getUserIdFromToken(token);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // extracting roles from token

                List<String> roleNames = jwtService.getRolesFromToken(token);
                Collection<GrantedAuthority> authorities = roleNames.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toSet());

                // Optionally load the full user details from DB (keeps latest roles/state)
                User user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null,
                                (authorities.isEmpty() ? user.getAuthorities() : authorities));

                // this contain details of user like IP Address, SessionID etc.
                // important for setting up rateLimiting or DDOS Attack
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ex) {
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}
