package com.lacodigoneta.elbuensabor.dto.product;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.dto.productdetails.ProductDetailDto;
import com.lacodigoneta.elbuensabor.validators.NoDuplicatedIngredients;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    @Null(message = NULL_VALIDATION_MESSAGE)
    private UUID id;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String name;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String description;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @PositiveOrZero
    private Integer cookingTime;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Valid
    private CategoryDto category;

    @Length(min = 1, message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String recipe;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private Boolean active;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private BigDecimal profitMargin;

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    @Size(min = 1, message = SIZE_VALIDATION_MESSAGE)
    @NoDuplicatedIngredients(message = DUPLICATED_INGREDIENTS_VALIDATION_MESSAGE)
    private List<@Valid ProductDetailDto> productDetails;

    @Null
    private ImageDto image;

}
