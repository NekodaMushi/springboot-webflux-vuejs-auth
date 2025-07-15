package com.fab1.backend.service;

import com.fab1.backend.dto.LoginRequest;
import com.fab1.backend.dto.LoginResponse;
import com.fab1.backend.model.Role;
import com.fab1.backend.model.User;
import com.fab1.backend.repository.RoleRepository;
import com.fab1.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Mono<LoginResponse> authenticate(LoginRequest loginRequest) {
        log.debug("Tentative d'authentification pour: {}", loginRequest.getUsername());

        return findUserByUsername(loginRequest.getUsername())
                .flatMap(user -> validatePassword(user, loginRequest.getPassword()))
                .flatMap(this::generateTokenForUser)
                .doOnSuccess(response -> log.info("Authentification réussie pour: {}",
                        loginRequest.getUsername()))
                .doOnError(error -> log.warn("Échec d'authentification pour {}: {}",
                        loginRequest.getUsername(), error.getMessage()));
    }

    private Mono<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé")));
    }

    private Mono<User> validatePassword(User user, String password) {
        return Mono.fromCallable(() -> passwordEncoder.matches(password, user.getPassword()))
                .flatMap(matches -> matches
                        ? Mono.just(user)
                        : Mono.error(new RuntimeException("Mot de passe incorrect")));
    }

    private Mono<LoginResponse> generateTokenForUser(User user) {
        return jwtService.generateToken(user)
                .map(token -> LoginResponse.success(user.getUsername(), token));
    }

    public Mono<User> createUser(String username, String password, String roleName) {
        log.debug("Création d'un nouvel utilisateur: {} avec rôle: {}", username, roleName);

        return checkUsernameAvailability(username)
                .then(findRoleByName(roleName))
                .flatMap(role -> createAndSaveUser(username, password, role))
                .doOnSuccess(user -> log.info("Utilisateur créé avec succès: {}", username))
                .doOnError(error -> log.error("Erreur lors de la création de l'utilisateur {}: {}",
                        username, error.getMessage()));
    }

    private Mono<Void> checkUsernameAvailability(String username) {
        return userRepository.existsByUsername(username)
                .flatMap(exists -> exists
                        ? Mono.error(new RuntimeException("L'utilisateur existe déjà"))
                        : Mono.empty());
    }

    private Mono<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .switchIfEmpty(Mono.error(new RuntimeException("Rôle non trouvé: " + roleName)));
    }

    private Mono<User> createAndSaveUser(String username, String password, Role role) {
        return Mono.fromCallable(() -> passwordEncoder.encode(password))
                .map(encodedPassword -> User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .enabled(true)
                        .build())
                .map(user -> user.withRole(role))
                .flatMap(userRepository::save);
    }
}