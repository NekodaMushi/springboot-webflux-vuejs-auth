package com.fab1.backend.dto;

import lombok.Builder;
import lombok.Value;
import com.fasterxml.jackson.annotation.JsonInclude;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String username;
    boolean enabled;
    String roleName;
}
