package com.fab1.backend.config;

import com.fab1.backend.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<LoginResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));

        log.warn("Erreur de validation: {}", errors);

        LoginResponse response = LoginResponse.failure("Données invalides: " + errors);
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<LoginResponse>> handleWebExchangeBindException(
            WebExchangeBindException ex) {

        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));

        log.warn("Erreur de validation WebFlux: {}", errors);

        LoginResponse response = LoginResponse.failure("Données invalides: " + errors);
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<LoginResponse>> handleRuntimeException(RuntimeException ex) {
        log.error("Erreur runtime: {}", ex.getMessage(), ex);

        LoginResponse response = LoginResponse.serverError();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<LoginResponse>> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);

        LoginResponse response = LoginResponse.serverError();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}