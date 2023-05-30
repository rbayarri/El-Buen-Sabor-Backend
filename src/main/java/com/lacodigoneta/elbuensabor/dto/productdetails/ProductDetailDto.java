package com.lacodigoneta.elbuensabor.dto.productdetails;

import com.lacodigoneta.elbuensabor.dto.ingredient.SimpleIngredientDto;
import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.validation.Valid;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Valid
    private SimpleIngredientDto ingredient;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private MeasurementUnit clientMeasurementUnit;

    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal quantity;

}
