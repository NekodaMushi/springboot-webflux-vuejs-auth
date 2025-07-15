package com.fab1.backend.service;

import com.fab1.backend.model.User;
import com.fab1.backend.model.Role;
import com.fab1.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.debug("Tentative de chargement réactif de l'utilisateur: {}", username);

        return userRepository.findByUsername(username)
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Utilisateur non trouvé: " + username)))
                .doOnSuccess(user -> {
                    Role role = ((User) user).getRole();
                    String roleName = role != null ? role.getName() : "AUCUN";
                    log.debug("Utilisateur chargé avec succès: {} avec rôle: {}", username, roleName);
                })
                .doOnError(error -> log.warn("Utilisateur non trouvé: {}", username));
    }

    /**
     * Grab a user by their username (returns User, not UserDetails)
     */
    public Mono<User> findUserByUsername(String username) {
        log.debug("Récupération de l'utilisateur: {}", username);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Utilisateur non trouvé: " + username)));
    }

    /**
     * Alias for getUserByUsername, for compatibility 
     */
    public Mono<User> getUserByUsername(String username) {
        return findUserByUsername(username);
    }

    /**
     * Nuke a user by their username
     */
    public Mono<Void> deleteUser(String username) {
        log.info("Suppression de l'utilisateur: {}", username);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé")))
                .flatMap(user -> userRepository.delete(user))
                .doOnSuccess(v -> log.info("Utilisateur supprimé avec succès: {}", username))
                .onErrorResume(error -> {
                    log.error("Erreur lors de la suppression de l'utilisateur {}: {}", username, error.getMessage());
                    return Mono.error(new RuntimeException("Erreur lors de la suppression"));
                });
    }

    /**
     * Check if a user exists, quick and easy
     */
    public Mono<Boolean> userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Count how many users we got in total
     */
    public Mono<Long> countUsers() {
        return userRepository.count();
    }

    /**
     * Flip a user's enabled status on or off
     */
    public Mono<User> setUserEnabled(String username, boolean enabled) {
        log.info("Modification du statut de l'utilisateur {}: enabled={}", username, enabled);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé")))
                .map(user -> user.withEnabled(enabled))
                .flatMap(userRepository::save)
                .doOnSuccess(user -> log.info("Statut modifié avec succès pour: {}", username));
    }
}
