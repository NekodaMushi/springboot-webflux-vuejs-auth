package com.fab1.backend.controller;

import com.fab1.backend.dto.LoginRequest;
import com.fab1.backend.dto.LoginResponse;
import com.fab1.backend.dto.UserResponse;
import com.fab1.backend.model.User;
import com.fab1.backend.service.AuthService;
import com.fab1.backend.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Tentative de connexion pour: {}", loginRequest.getUsername());

        return authService.authenticate(loginRequest)
                .onErrorResume(this::handleAuthenticationError)
                .doOnSuccess(response -> {
                    if (response.isSuccess()) {
                        log.info("Connexion réussie pour: {}", loginRequest.getUsername());
                    } else {
                        log.warn("Connexion échouée pour: {}", loginRequest.getUsername());
                    }
                });
    }

    @PostMapping("/register")
    public Mono<LoginResponse> register(@Valid @RequestBody LoginRequest registerRequest) {
        log.info("Tentative d'inscription pour: {}", registerRequest.getUsername());

        return authService.createUser(
                        registerRequest.getUsername(),
                        registerRequest.getPassword(),
                        "USER"
                )
                .then(authService.authenticate(registerRequest))
                .onErrorResume(this::handleRegistrationError)
                .doOnSuccess(response -> log.info("Inscription et connexion réussies pour: {}",
                        registerRequest.getUsername()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<LoginResponse>> logout(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        log.info("Déconnexion pour: {}", username);
        
        return Mono.just(ResponseEntity.ok(
                LoginResponse.success(username, null).withMessage("Déconnexion réussie")
        ));
    }

    @DeleteMapping("/delete-account")
    public Mono<ResponseEntity<LoginResponse>> deleteAccount(Authentication authentication) {
        String username = authentication.getName();
        log.info("Suppression du compte pour: {}", username);

        return userDetailsService.deleteUser(username)
                .then(Mono.just(ResponseEntity.ok(
                        LoginResponse.success(username, null).withMessage("Compte supprimé avec succès")
                )))
                .onErrorResume(error -> {
                    log.error("Erreur lors de la suppression du compte {}: {}", username, error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(
                            LoginResponse.failure("Erreur lors de la suppression du compte")
                    ));
                });
    }

    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Récupération des informations pour: {}", username);

        return userDetailsService.getUserByUsername(username)
                .map(user -> UserResponse.builder()
                        .username(user.getUsername())
                        .enabled(user.isEnabled())
                        .roleName(user.getRoleName())
                        .build())
                .onErrorResume(error -> {
                    log.error("Erreur lors de la récupération de l'utilisateur {}: {}", username, error.getMessage());
                    return Mono.error(new RuntimeException("Utilisateur non trouvé"));
                });
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("API réactive fonctionne parfaitement !")
                .doOnSuccess(message -> log.debug("Test endpoint appelé"));
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Service d'authentification opérationnel")
                .doOnSuccess(message -> log.debug("Health check effectué"));
    }

    private Mono<LoginResponse> handleAuthenticationError(Throwable error) {
        log.error("Erreur d'authentification: {}", error.getMessage());

        if (error.getMessage().contains("Mot de passe incorrect") ||
                error.getMessage().contains("Utilisateur non trouvé")) {
            return Mono.just(LoginResponse.failure("Identifiants incorrects"));
        }

        return Mono.just(LoginResponse.serverError());
    }

    private Mono<LoginResponse> handleRegistrationError(Throwable error) {
        log.error("Erreur d'inscription: {}", error.getMessage());

        if (error.getMessage().contains("L'utilisateur existe déjà")) {
            return Mono.just(LoginResponse.failure("Ce nom d'utilisateur est déjà pris"));
        }

        if (error.getMessage().contains("Rôle non trouvé")) {
            return Mono.just(LoginResponse.failure("Erreur de configuration des rôles"));
        }

        return Mono.just(LoginResponse.serverError());
    }
}
