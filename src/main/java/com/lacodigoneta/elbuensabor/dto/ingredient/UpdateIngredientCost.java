package com.lacodigoneta.elbuensabor.dto.ingredient;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;
import static com.lacodigoneta.elbuensabor.config.AppConstants.POSITIVE_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateIngredientCost {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private MeasurementUnit clientMeasurementUnit;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal updatedCost;

}
