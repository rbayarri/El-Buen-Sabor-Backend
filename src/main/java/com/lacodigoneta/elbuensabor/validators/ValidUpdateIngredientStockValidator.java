package com.lacodigoneta.elbuensabor.validators;

import com.lacodigoneta.elbuensabor.dto.ingredient.UpdateIngredientStock;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class ValidUpdateIngredientStockValidator implements ConstraintValidator<ValidUpdateIngredientStock, UpdateIngredientStock> {
    @Override
    public void initialize(ValidUpdateIngredientStock constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UpdateIngredientStock value, ConstraintValidatorContext context) {

        int nonNullFieldsCounter = 0;

        Field[] declaredFields = value.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                if (Objects.nonNull(fieldValue)) {
                    nonNullFieldsCounter++;
                }

            } catch (IllegalAccessException e) {
                throw new ConstraintDeclarationException("Could not access field '" + fieldName + "' on class " + value.getClass().getName(), e);
            }finally {
                field.setAccessible(false);
            }
        }

        return nonNullFieldsCounter == 2;
    }
}
