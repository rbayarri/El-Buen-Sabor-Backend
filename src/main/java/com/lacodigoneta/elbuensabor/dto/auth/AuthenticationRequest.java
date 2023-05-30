package com.lacodigoneta.elbuensabor.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Email(message = EMAIL_VALIDATION_MESSAGE)
    private String username;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String password;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Boolean rememberMe;

}
