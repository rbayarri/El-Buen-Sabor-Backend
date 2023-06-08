package com.lacodigoneta.elbuensabor.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lacodigoneta.elbuensabor.config.AppConstants.EMAIL_VALIDATION_MESSAGE;
import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetPasswordRequest {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Email(message = EMAIL_VALIDATION_MESSAGE)
    private String username;

}
