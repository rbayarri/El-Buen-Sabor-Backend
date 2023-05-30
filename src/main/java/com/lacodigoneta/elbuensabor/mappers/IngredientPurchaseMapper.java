package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.IngredientPurchaseDto;
import com.lacodigoneta.elbuensabor.dto.ingredient.SimpleIngredientDto;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.IngredientPurchase;
import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import com.lacodigoneta.elbuensabor.exceptions.IncompatibleMeasurementUnitTypeException;
import com.lacodigoneta.elbuensabor.services.IngredientService;
import com.lacodigoneta.elbuensabor.utilities.MeasurementUnitConversion;
import com.lacodigoneta.elbuensabor.utilities.PriceConvertor;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@AllArgsConstructor
public class IngredientPurchaseMapper {

    private final ModelMapper mapper;

    private final IngredientService ingredientService;

    public IngredientPurchaseDto toDto(IngredientPurchase ingredientPurchase) {
        IngredientPurchaseDto ingredientPurchaseDto = mapper.map(ingredientPurchase, IngredientPurchaseDto.class);

        ingredientPurchaseDto.setIngredient(mapper.map(ingredientPurchase.getIngredient(), SimpleIngredientDto.class));

        ingredientPurchaseDto.setDate(ingredientPurchase.getDateTime().toLocalDate());

        MeasurementUnit ingredientMeasurementUnit = ingredientPurchase.getIngredient().getMeasurementUnit();

        ingredientPurchaseDto.setQuantity(MeasurementUnitConversion.convert(ingredientPurchase.getQuantity(),
                ingredientMeasurementUnit,
                ingredientPurchase.getClientMeasurementUnit()));

        ingredientPurchaseDto.setUnitPrice(PriceConvertor.convert(ingredientPurchase.getUnitPrice(),
                ingredientMeasurementUnit,
                ingredientPurchase.getClientMeasurementUnit()));

        return ingredientPurchaseDto;
    }

    public IngredientPurchase toEntity(IngredientPurchaseDto ingredientPurchaseDto) {
        IngredientPurchase ingredientPurchase = mapper.map(ingredientPurchaseDto, IngredientPurchase.class);

        LocalDate purchaseDate = ingredientPurchaseDto.getDate();
        LocalDateTime purchaseDatetime = null;
        if (purchaseDate.isBefore(LocalDate.now())) {
            purchaseDatetime = LocalDateTime.of(purchaseDate, LocalTime.MAX);
        } else {
            purchaseDatetime = LocalDateTime.now();
        }
        ingredientPurchase.setDateTime(purchaseDatetime);

        Ingredient ingredient = ingredientService.findById(ingredientPurchaseDto.getIngredient().getId());
        ingredientPurchase.setIngredient(ingredient);

        if (!ingredientPurchaseDto.getClientMeasurementUnit().getType().equals(ingredient.getMeasurementUnit().getType())) {
            throw new IncompatibleMeasurementUnitTypeException(ingredientPurchaseDto.getClientMeasurementUnit(), ingredient.getMeasurementUnit());
        }

        ingredientPurchase.setQuantity(MeasurementUnitConversion.convert(ingredientPurchaseDto.getQuantity(),
                ingredientPurchaseDto.getClientMeasurementUnit(),
                ingredient.getMeasurementUnit()));

        ingredientPurchase.setUnitPrice(PriceConvertor.convert(ingredientPurchaseDto.getUnitPrice(),
                ingredientPurchaseDto.getClientMeasurementUnit(),
                ingredient.getMeasurementUnit()));

        return ingredientPurchase;
    }
}
