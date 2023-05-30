package com.lacodigoneta.elbuensabor.dto.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_EMPTY_VALIDATION_MESSAGE;
import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEditCategoryDto {

    private UUID id;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String name;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Boolean container;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Boolean active;

    @Valid
    private CategoryDto parent;
}
