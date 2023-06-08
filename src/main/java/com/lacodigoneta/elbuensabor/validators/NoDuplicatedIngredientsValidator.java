package com.lacodigoneta.elbuensabor.validators;

import com.lacodigoneta.elbuensabor.dto.productdetails.ProductDetailDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NoDuplicatedIngredientsValidator implements ConstraintValidator<NoDuplicatedIngredients, List<ProductDetailDto>> {
    @Override
    public void initialize(NoDuplicatedIngredients constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<ProductDetailDto> value, ConstraintValidatorContext context) {
        try {
            return value.stream().map(pd -> pd.getIngredient().getId()).distinct().count() == value.size();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request");
        }
    }
}
