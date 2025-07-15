package com.fab1.backend.config;

import com.fab1.backend.model.Role;
import com.fab1.backend.model.User;
import com.fab1.backend.repository.RoleRepository;
import com.fab1.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("🚀 Initialisation des données...");

        createRolesIfNotExist()
                .then(createUsersIfNotExist())
                .doOnSuccess(unused -> log.info("✅ Initialisation des données terminée"))
                .doOnError(error -> log.error("❌ Erreur lors de l'initialisation: {}", error.getMessage()))
                .subscribe();
                /* Not blocking, executed in background*/
    }

    private Mono<Void> createRolesIfNotExist() {
        log.debug("Création des rôles...");

        return Mono.when(
                createRoleIfNotExists("USER", "Utilisateur standard"),
                createRoleIfNotExists("ADMIN", "Administrateur système"),
                createRoleIfNotExists("MODERATOR", "Modérateur")
        );
    }

    private Mono<Void> createRoleIfNotExists(String roleName, String description) {
        return roleRepository.existsByName(roleName)
                .flatMap(exists -> {
                    if (!exists) {
                        Role role = new Role(roleName, description);
                        return roleRepository.save(role)
                                .doOnSuccess(r -> log.info("✅ Rôle {} créé", roleName))
                                .then();
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> createUsersIfNotExist() {
        log.debug("Création des utilisateurs...");

        return Mono.when(
                createAdminUser(),
                createStandardUser(),
                createModeratorUser()
        );
    }

    private Mono<Void> createAdminUser() {
        return userRepository.existsByUsername("admin")
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.zip(
                                        roleRepository.findByName("ADMIN"),
                                        roleRepository.findByName("USER")
                                ).flatMap(tuple -> {
                                    Role adminRole = tuple.getT1();
                                    Role userRole = tuple.getT2();

                                    User admin = User.builder()
                                            .username("admin")
                                            .password(passwordEncoder.encode("password"))
                                            .enabled(true)
                                            .roles(Set.of(adminRole, userRole))
                                            .build();

                                    return userRepository.save(admin);
                                }).doOnSuccess(user -> log.info("✅ Utilisateur admin créé (admin/password)"))
                                .then();
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> createStandardUser() {
        return userRepository.existsByUsername("user")
                .flatMap(exists -> {
                    if (!exists) {
                        return roleRepository.findByName("USER")
                                .flatMap(userRole -> {
                                    User user = User.builder()
                                            .username("user")
                                            .password(passwordEncoder.encode("123456"))
                                            .enabled(true)
                                            .roles(Set.of(userRole))
                                            .build();

                                    return userRepository.save(user);
                                }).doOnSuccess(user -> log.info("✅ Utilisateur user créé (user/123456)"))
                                .then();
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> createModeratorUser() {
        return userRepository.existsByUsername("moderator")
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.zip(
                                        roleRepository.findByName("MODERATOR"),
                                        roleRepository.findByName("USER")
                                ).flatMap(tuple -> {
                                    Role moderatorRole = tuple.getT1();
                                    Role userRole = tuple.getT2();

                                    User moderator = User.builder()
                                            .username("moderator")
                                            .password(passwordEncoder.encode("mod123"))
                                            .enabled(true)
                                            .roles(Set.of(moderatorRole, userRole))
                                            .build();

                                    return userRepository.save(moderator);
                                }).doOnSuccess(user -> log.info("✅ Utilisateur moderator créé (moderator/mod123)"))
                                .then();
                    }
                    return Mono.empty();
                });
    }
}