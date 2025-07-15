package com.fab1.backend.config;

import com.fab1.backend.service.CustomUserDetailsService;
import com.fab1.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService());
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/auth/test").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/auth/health").permitAll()

                        // Admin endpoints
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")

                        // anything else needs auth
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        // 404 & 403
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public WebFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, reactiveUserDetailsService());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            log.warn("Tentative d'accès non autorisée: {}", ex.getMessage());

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> body = new HashMap<>();
            body.put("error", "Non autorisé");
            body.put("message", "Token JWT manquant ou invalide");
            body.put("timestamp", System.currentTimeMillis());
            body.put("path", exchange.getRequest().getPath().value());

            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(body);
                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes()))
                );
            } catch (Exception e) {
                log.error("Erreur lors de la sérialisation JSON: {}", e.getMessage());
                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap("Unauthorized".getBytes()))
                );
            }
        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            log.warn("Accès refusé pour l'utilisateur: {}", denied.getMessage());

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> body = new HashMap<>();
            body.put("error", "Accès refusé");
            body.put("message", "Permissions insuffisantes");
            body.put("timestamp", System.currentTimeMillis());
            body.put("path", exchange.getRequest().getPath().value());

            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(body);
                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes()))
                );
            } catch (Exception e) {
                log.error("Erreur lors de la sérialisation JSON: {}", e.getMessage());
                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap("Forbidden".getBytes()))
                );
            }
        };
    }

    @PostConstruct
    public void checkBeans() {
        log.info("JwtService bean: {}", jwtService);
        log.info("CustomUserDetailsService bean: {}", customUserDetailsService);
    }

    // JWT reactif filter
    @RequiredArgsConstructor
    public static class JwtAuthenticationFilter implements WebFilter {

        private final JwtService jwtService;
        private final ReactiveUserDetailsService userDetailsService;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            String path = exchange.getRequest().getPath().value();

            // Skip JWT validation for public endpoints
            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            return extractTokenFromRequest(exchange)
                    .flatMap(token ->
                            jwtService.extractUsername(token)
                                    .flatMap(username ->
                                            jwtService.validateToken(token, username)
                                                    .filter(Boolean::booleanValue)
                                                    .flatMap(valid -> userDetailsService.findByUsername(username))
                                                    .flatMap(userDetails -> {
                                                        Authentication authentication =
                                                                new UsernamePasswordAuthenticationToken(
                                                                        userDetails, null, userDetails.getAuthorities());

                                                        return chain.filter(exchange)
                                                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                                                    })
                                    )
                    )
                    .switchIfEmpty(chain.filter(exchange))
                    .onErrorResume(error -> {
                        log.debug("JWT validation failed: {}", error.getMessage());
                        return chain.filter(exchange);
                    });
        }

        private Mono<String> extractTokenFromRequest(ServerWebExchange exchange) {
            return Mono.fromSupplier(() -> {
                String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader.substring(7);
                }
                return null;
            }).filter(token -> token != null);
        }

        private boolean isPublicPath(String path) {
            return path.startsWith("/api/auth/") ||
                    path.startsWith("/h2-console/") ||
                    path.equals("/api/auth/login") ||
                    path.equals("/api/auth/register") ||
                    path.equals("/api/auth/test") ||
                    path.equals("/api/auth/health");
        }
    }
}