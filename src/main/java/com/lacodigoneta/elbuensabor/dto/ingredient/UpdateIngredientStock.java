package com.lacodigoneta.elbuensabor.dto.ingredient;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import com.lacodigoneta.elbuensabor.validators.ValidUpdateIngredientStock;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidUpdateIngredientStock
public class UpdateIngredientStock {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private MeasurementUnit clientMeasurementUnit;

    @PositiveOrZero(message = POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private BigDecimal newStock;

    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal stockLoss;

    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal stockGain;

}
