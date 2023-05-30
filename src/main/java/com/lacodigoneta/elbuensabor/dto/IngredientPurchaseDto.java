package com.lacodigoneta.elbuensabor.dto;

import com.lacodigoneta.elbuensabor.dto.ingredient.SimpleIngredientDto;
import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientPurchaseDto {

    @Null(message = NULL_VALIDATION_MESSAGE)
    private UUID id;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private LocalDate date;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private SimpleIngredientDto ingredient;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private MeasurementUnit clientMeasurementUnit;

    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal quantity;

    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal unitPrice;
}
