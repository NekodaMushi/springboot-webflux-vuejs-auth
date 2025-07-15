package com.fab1.backend.repository;

import com.fab1.backend.model.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveCrudRepository<Role, Long> {
    Mono<Role> findByName(String name);
    Mono<Boolean> existsByName(String name);
}