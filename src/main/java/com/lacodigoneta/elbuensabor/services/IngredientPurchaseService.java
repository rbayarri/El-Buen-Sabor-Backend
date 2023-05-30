package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.dto.ingredient.UpdateIngredientCost;
import com.lacodigoneta.elbuensabor.dto.ingredient.UpdateIngredientStock;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.IngredientPurchase;
import com.lacodigoneta.elbuensabor.entities.Profit;
import com.lacodigoneta.elbuensabor.exceptions.IncompatibleMeasurementUnitTypeException;
import com.lacodigoneta.elbuensabor.exceptions.IngredientException;
import com.lacodigoneta.elbuensabor.exceptions.IngredientPurchaseException;
import com.lacodigoneta.elbuensabor.repositories.IngredientPurchaseRepository;
import com.lacodigoneta.elbuensabor.utilities.MeasurementUnitConversion;
import com.lacodigoneta.elbuensabor.utilities.PriceConvertor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INACTIVE_INGREDIENT;
import static com.lacodigoneta.elbuensabor.config.AppConstants.INVALID_DATE_FOR_INGREDIENT_PURCHASE;

@Service
public class IngredientPurchaseService extends BaseServiceImpl<IngredientPurchase, IngredientPurchaseRepository> {

    private final IngredientService ingredientService;

    private final ProfitService profitService;

    public IngredientPurchaseService(IngredientPurchaseRepository repository, IngredientService ingredientService, ProfitService profitService) {
        super(repository);
        this.ingredientService = ingredientService;
        this.profitService = profitService;
    }

    @Override
    public IngredientPurchase changeStates(IngredientPurchase source, IngredientPurchase destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(IngredientPurchase entity) {

        if (!entity.getIngredient().isActive()) {
            throw new IngredientException(INACTIVE_INGREDIENT);
        }

        List<IngredientPurchase> ingredientsPurchasesAfter = repository.findAllByIngredientIdAndDateTimeAfter(entity.getIngredient().getId(), entity.getDateTime());
        if (Objects.nonNull(ingredientsPurchasesAfter) && ingredientsPurchasesAfter.size() > 0) {
            throw new IngredientPurchaseException(INVALID_DATE_FOR_INGREDIENT_PURCHASE);
        }

        // Register holding results

        ingredientService.addLastCost(entity.getIngredient());
        ingredientService.addCurrentStock(entity.getIngredient());

        Profit profit = Profit.builder()
                .dateTime(entity.getDateTime())
                .amount((entity.getUnitPrice().subtract(entity.getIngredient().getLastCost())).multiply(entity.getIngredient().getCurrentStock()))
                .build();

        profitService.save(profit);

    }

    public Ingredient updateCost(UUID id, UpdateIngredientCost updateIngredientCost) {
        Ingredient ingredient = ingredientService.findById(id);

        if (!ingredient.getMeasurementUnit().getType().equals(updateIngredientCost.getClientMeasurementUnit().getType())) {
            throw new IncompatibleMeasurementUnitTypeException(ingredient.getMeasurementUnit(), updateIngredientCost.getClientMeasurementUnit());
        }

        IngredientPurchase ingredientPurchase = IngredientPurchase.builder()
                .dateTime(LocalDateTime.now())
                .ingredient(ingredient)
                .clientMeasurementUnit(updateIngredientCost.getClientMeasurementUnit())
                .quantity(BigDecimal.ZERO)
                .build();

        if (!ingredient.getMeasurementUnit().equals(updateIngredientCost.getClientMeasurementUnit())) {
            ingredientPurchase.setUnitPrice(
                    PriceConvertor.convert(
                            updateIngredientCost.getUpdatedCost(),
                            updateIngredientCost.getClientMeasurementUnit(),
                            ingredient.getMeasurementUnit()
                    )
            );
        } else {
            ingredientPurchase.setUnitPrice(updateIngredientCost.getUpdatedCost());
        }

        IngredientPurchase savedIngredientPurchase = save(ingredientPurchase);
        ingredient.getIngredientPurchaseList().add(savedIngredientPurchase);

        return ingredientService.completeEntity(ingredient);
    }

    public Ingredient updateStock(UUID id, UpdateIngredientStock updateIngredientStock) {

        Ingredient ingredient = ingredientService.findById(id);

        if (!ingredient.getMeasurementUnit().getType().equals(updateIngredientStock.getClientMeasurementUnit().getType())) {
            throw new IncompatibleMeasurementUnitTypeException(ingredient.getMeasurementUnit(), updateIngredientStock.getClientMeasurementUnit());
        }

        BigDecimal purchaseQuantity = null;

        if (Objects.nonNull(updateIngredientStock.getNewStock())) {
            BigDecimal newQuantity = MeasurementUnitConversion.convert(
                    updateIngredientStock.getNewStock(),
                    updateIngredientStock.getClientMeasurementUnit(),
                    ingredient.getMeasurementUnit());

            purchaseQuantity = newQuantity.subtract(ingredient.getCurrentStock());

        } else if (Objects.nonNull(updateIngredientStock.getStockLoss())) {
            purchaseQuantity = MeasurementUnitConversion.convert(
                    updateIngredientStock.getStockLoss(),
                    updateIngredientStock.getClientMeasurementUnit(),
                    ingredient.getMeasurementUnit()).negate();

        } else {
            purchaseQuantity = MeasurementUnitConversion.convert(
                    updateIngredientStock.getStockGain(),
                    updateIngredientStock.getClientMeasurementUnit(),
                    ingredient.getMeasurementUnit());
        }

        IngredientPurchase ingredientPurchase = IngredientPurchase.builder()
                .dateTime(LocalDateTime.now())
                .ingredient(ingredient)
                .clientMeasurementUnit(updateIngredientStock.getClientMeasurementUnit())
                .quantity(purchaseQuantity)
                .unitPrice(ingredient.getLastCost())
                .build();

        IngredientPurchase savedIngredientPurchase = save(ingredientPurchase);
        ingredient.getIngredientPurchaseList().add(savedIngredientPurchase);

        return ingredientService.completeEntity(ingredient);

    }
}
