package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.ingredient.SimpleIngredientDto;
import com.lacodigoneta.elbuensabor.dto.productdetails.ProductDetailDto;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.ProductDetail;
import com.lacodigoneta.elbuensabor.exceptions.IncompatibleMeasurementUnitTypeException;
import com.lacodigoneta.elbuensabor.exceptions.ProductException;
import com.lacodigoneta.elbuensabor.services.IngredientService;
import com.lacodigoneta.elbuensabor.utilities.MeasurementUnitConversion;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INACTIVE_INGREDIENTS;

@Service
@AllArgsConstructor
public class ProductDetailMapper {

    private final ModelMapper mapper;

    private final IngredientService ingredientService;

    public ProductDetail toProductDetail(ProductDetailDto productDetailDto) {

        Ingredient ingredientDatabase = ingredientService.findById(productDetailDto.getIngredient().getId());

        checkIfIsValidIngredient(productDetailDto, ingredientDatabase);

        BigDecimal quantity = productDetailDto.getQuantity();

        if (!ingredientDatabase.getMeasurementUnit().equals(productDetailDto.getClientMeasurementUnit())) {
            quantity = MeasurementUnitConversion.convert(productDetailDto.getQuantity(), productDetailDto.getClientMeasurementUnit(), ingredientDatabase.getMeasurementUnit());
        }

        return ProductDetail.builder()
                .ingredient(ingredientDatabase)
                .quantity(quantity)
                .clientMeasurementUnit(productDetailDto.getClientMeasurementUnit())
                .build();
    }

    public ProductDetailDto toProductDetailDto(ProductDetail productDetail) {

        return ProductDetailDto.builder()
                .ingredient(mapper.map(productDetail.getIngredient(), SimpleIngredientDto.class))
                .clientMeasurementUnit(productDetail.getClientMeasurementUnit())
                .quantity(MeasurementUnitConversion.convert(productDetail.getQuantity(), productDetail.getIngredient().getMeasurementUnit(), productDetail.getClientMeasurementUnit()))
                .build();
    }

    private void checkIfIsValidIngredient(ProductDetailDto productDetailDto, Ingredient ingredientDatabase) {

        if (!ingredientDatabase.isActive()) {
            throw new ProductException(INACTIVE_INGREDIENTS);
        }

        if (!ingredientDatabase.getMeasurementUnit().getType().equals(productDetailDto.getClientMeasurementUnit().getType())) {
            throw new IncompatibleMeasurementUnitTypeException(ingredientDatabase.getMeasurementUnit(), productDetailDto.getClientMeasurementUnit());
        }
    }
}
