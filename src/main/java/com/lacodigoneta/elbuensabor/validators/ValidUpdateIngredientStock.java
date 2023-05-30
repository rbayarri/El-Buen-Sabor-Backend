package com.lacodigoneta.elbuensabor.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NEW_STOCK_OR_STOCK_LOSS_OR_STOCK_GAIN;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUpdateIngredientStockValidator.class)
public @interface ValidUpdateIngredientStock {

    String message() default NEW_STOCK_OR_STOCK_LOSS_OR_STOCK_GAIN;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
