package com.fab1.backend.controller;

import com.fab1.backend.dto.LoginRequest;
import com.fab1.backend.dto.LoginResponse;
import com.fab1.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

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
                .then(authService.authenticate(registerRequest)) // Auto-login after registration
                .onErrorResume(this::handleRegistrationError)
                .doOnSuccess(response -> log.info("Inscription et connexion réussies pour: {}",
                        registerRequest.getUsername()));
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
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        // Now only erase token, later need to create blacklist on db
        return Mono.just(ResponseEntity.ok().build());
    }
}