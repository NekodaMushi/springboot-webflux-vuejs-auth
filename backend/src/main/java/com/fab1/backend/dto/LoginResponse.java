package com.fab1.backend.dto;

import lombok.Value;
import lombok.With;
import com.fasterxml.jackson.annotation.JsonInclude;

@Value
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    boolean success;
    String message;
    String username;
    String token;

    public static LoginResponse success(String username, String token) {
        return new LoginResponse(true, "Connexion réussie", username, token);

    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false,message,null,null);
    }

    public static LoginResponse serverError() {
        return new LoginResponse(false, "Erreur serveur", null, null);
    }
}