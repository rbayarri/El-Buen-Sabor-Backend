package com.lacodigoneta.elbuensabor.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;
import static com.lacodigoneta.elbuensabor.config.AppConstants.PASSWORD_PATTERN_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPassword {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$", message = PASSWORD_PATTERN_VALIDATION_MESSAGE)
    private String newPassword;

}
