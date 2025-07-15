package com.fab1.backend.service;

import com.fab1.backend.model.User;
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
                .doOnSuccess(user -> log.debug("Utilisateur chargé avec succès: {} avec {} rôle(s)",
                        username, ((User) user).getRoles().size()))
                .doOnError(error -> log.warn("Utilisateur non trouvé: {}", username));
    }

    // Util to get User (not UserDetails)
    public Mono<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Utilisateur non trouvé: " + username)));
    }
}