package com.org.AirBnB.security;

import com.org.AirBnB.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWTService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 2;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        // validate secret length - Keys.hmacShaKeyFor requires sufficiently long key bytes
        if (jwtSecretKey == null || jwtSecretKey.trim().length() < 32) {
            throw new IllegalStateException("jwt.secretKey must be set and at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        // setting expiration date for token
        Date now = new Date();
        Date exp = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);
        log.info("Access token generated");
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey)
                .compact();

    }

    public String generateRefreshToken(User user) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMonths(6).toInstant());
        log.info("Refresh token generated");
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Parses the token and returns the Claims if token is valid (signature + structure).
     * Throws JwtException on invalid token (expired, malformed, signature invalid).
     */
    private Claims parseClaims(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token); // throws JwtException on problems
        return jws.getBody();
    }

    /**
     * Returns userId (subject) from token. Throws JwtException if token invalid/expired.
     */
    public Long getUserIdFromToken(String token) throws JwtException {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        return subject == null ? null : Long.valueOf(subject);
    }

    /**
     * Returns roles that were embedded into the token as List<String>.
     * Throws JwtException if token invalid.
     */
    public List<String> getRolesFromToken(String token) throws JwtException {
        Claims claims = parseClaims(token);
        Object raw = claims.get("roles");
        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream()
                    .filter(o -> o != null)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Simple token validity check (signature + expiration).
     * Returns true if token is correctly signed and not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp == null || exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
