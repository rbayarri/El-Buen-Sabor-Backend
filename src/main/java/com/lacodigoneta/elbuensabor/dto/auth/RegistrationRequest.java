package com.lacodigoneta.elbuensabor.dto.auth;

import com.lacodigoneta.elbuensabor.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Email(message = EMAIL_VALIDATION_MESSAGE)
    private String username;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$", message = PASSWORD_PATTERN_VALIDATION_MESSAGE)
    private String password;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Role role;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String name;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String lastName;

}
