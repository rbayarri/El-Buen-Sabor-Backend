package com.lacodigoneta.elbuensabor.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private UUID id;

    private String name;

}
