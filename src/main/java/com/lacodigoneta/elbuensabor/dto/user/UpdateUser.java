package com.lacodigoneta.elbuensabor.dto.user;

import com.lacodigoneta.elbuensabor.enums.Role;
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
public class UpdateUser {

    private Role role;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String name;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String lastName;

    private boolean active;

    private String username;


}
