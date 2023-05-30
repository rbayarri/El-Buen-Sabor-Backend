package com.lacodigoneta.elbuensabor.dto.ingredient;

import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientDto {

    private UUID id;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String name;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Valid
    private CategoryDto category;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private MeasurementUnit measurementUnit;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private BigDecimal currentStock;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @PositiveOrZero(message = POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private BigDecimal minimumStock;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private BigDecimal lastCost;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Boolean active;

}
