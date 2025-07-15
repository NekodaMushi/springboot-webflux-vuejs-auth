package com.fab1.backend;

import com.fab1.backend.dto.LoginRequest;
import com.fab1.backend.dto.LoginResponse;
import com.fab1.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.findByUsername("testuser")
                .flatMap(user -> userRepository.deleteById(user.getId()))
                .block();
    }

    @Test
    void testHealthEndpoint() {
        webTestClient.get()
                .uri("/api/auth/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Service d'authentification opérationnel");
    }

    @Test
    void testRegisterAndLogin() {

        LoginRequest registerRequest = new LoginRequest("testuser", "password123");

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(resp -> {
                    assert resp.isSuccess();
                });


        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(resp -> {
                    assert resp.isSuccess();
                    assert resp.getToken() != null && !resp.getToken().isEmpty();
                });
    }
}