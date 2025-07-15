package com.fab1.backend.service;

import com.fab1.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Mono<String> generateToken(User user) {
        return Mono.fromCallable(() -> {
                    Map<String, Object> claims = new HashMap<>();

                    // Add roles in token
                    String roles = user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));

                    claims.put("roles", roles);
                    claims.put("userId", user.getId());

                    return createToken(claims, user.getUsername());
                })
                .doOnSuccess(token -> log.debug("Token JWT généré pour l'utilisateur: {}", user.getUsername()))
                .doOnError(error -> log.error("Erreur lors de la génération du token pour {}: {}",
                        user.getUsername(), error.getMessage()));
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Mono<String> extractUsername(String token) {
        return extractClaims(token)
                .map(Claims::getSubject);
    }

    public Mono<String> extractRoles(String token) {
        return extractClaims(token)
                .map(claims -> (String) claims.get("roles"));
    }

    public Mono<Date> extractExpiration(String token) {
        return extractClaims(token)
                .map(Claims::getExpiration);
    }

    private Mono<Claims> extractClaims(String token) {
        return Mono.fromCallable(() -> {
                    return Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
                })
                .doOnError(error -> log.error("Erreur lors de l'extraction des claims: {}", error.getMessage()));
    }

    public Mono<Boolean> isTokenExpired(String token) {
        return extractExpiration(token)
                .map(expiration -> expiration.before(new Date()));
    }

    public Mono<Boolean> validateToken(String token, String username) {
        return extractUsername(token)
                .zipWith(isTokenExpired(token))
                .map(tuple -> {
                    String tokenUsername = tuple.getT1();
                    Boolean expired = tuple.getT2();
                    return tokenUsername.equals(username) && !expired;
                });
    }
}