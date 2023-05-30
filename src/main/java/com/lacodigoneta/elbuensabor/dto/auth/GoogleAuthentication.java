package com.lacodigoneta.elbuensabor.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_EMPTY_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleAuthentication {

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String googleToken;

}
