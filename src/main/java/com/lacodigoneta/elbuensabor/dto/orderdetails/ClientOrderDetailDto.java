package com.lacodigoneta.elbuensabor.dto.orderdetails;

import com.lacodigoneta.elbuensabor.dto.product.SimpleProductDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
public class ClientOrderDetailDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private SimpleProductDto product;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private Integer quantity;

    @Null(message = NULL_VALIDATION_MESSAGE)
    @Positive(message = POSITIVE_VALIDATION_MESSAGE)
    private BigDecimal unitPrice;

    @Null(message = NULL_VALIDATION_MESSAGE)
    @PositiveOrZero(message = POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private BigDecimal discount;

}
