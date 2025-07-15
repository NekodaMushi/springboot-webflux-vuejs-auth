/*
package com.fab1.backend.controller;

import com.fab1.backend.dto.LoginRequest;
import com.fab1.backend.dto.LoginResponse;
import com.fab1.backend.model.User;
import com.fab1.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TestController {

    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello depuis le backend !";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend Spring Boot opérationnel ✅";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return new LoginResponse(true, "Yihaa Login réussi poto", user.getUsername());

            }
        }
        return new LoginResponse(false, "Bad luck Brian", loginRequest.getUsername());
    }

    @GetMapping("/users")
    public String getUsers() {
        return "Nombre d'utilisateurs : " + userRepository.count();
    }
}
*/
