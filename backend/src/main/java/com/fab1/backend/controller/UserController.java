package com.fab1.backend.controller;

import com.fab1.backend.model.User;
import com.fab1.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public Mono<String> getCurrentUser(Principal principal) {
        return Mono.just("Utilisateur connecté: " + principal.getName())
                .doOnSuccess(info -> log.debug("Info utilisateur demandée pour: {}",
                        principal.getName()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> createUser(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String role) {
        log.info("Création d'utilisateur demandée: {} avec rôle: {}", username, role);

        return authService.createUser(username, password, role)
                .map(user -> "Utilisateur créé avec succès: " + user.getUsername())
                .onErrorResume(error -> {
                    log.error("Erreur lors de la création d'utilisateur: {}", error.getMessage());
                    return Mono.just("Erreur: " + error.getMessage());
                });
    }

    @GetMapping("/test-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> testAdmin() {
        return Mono.just("Accès admin autorisé !")
                .doOnSuccess(message -> log.debug("Endpoint admin testé"));
    }

    @GetMapping("/test-user")
    @PreAuthorize("hasRole('USER')")
    public Mono<String> testUser() {
        return Mono.just("Accès utilisateur autorisé !")
                .doOnSuccess(message -> log.debug("Endpoint user testé"));
    }
}