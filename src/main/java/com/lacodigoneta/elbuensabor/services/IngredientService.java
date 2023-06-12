package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.IngredientPurchase;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.enums.CategoryType;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.exceptions.CategoryException;
import com.lacodigoneta.elbuensabor.exceptions.IngredientException;
import com.lacodigoneta.elbuensabor.exceptions.InvalidParentException;
import com.lacodigoneta.elbuensabor.repositories.IngredientRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Service
public class IngredientService extends BaseServiceImpl<Ingredient, IngredientRepository> {

    private final CategoryService categoryService;

    public IngredientService(IngredientRepository repository, CategoryService categoryService) {
        super(repository);
        this.categoryService = categoryService;
    }

    public List<Ingredient> findAllByActive() {
        return repository.findAllByActiveTrue().stream().map(this::completeEntity).collect(Collectors.toList());
    }

    @Override
    public Ingredient changeStates(Ingredient source, Ingredient destination) {

        if (!destination.getMeasurementUnit().equals(source.getMeasurementUnit())) {
            throw new IngredientException(MEASUREMENT_UNIT_CANNOT_BE_CHANGED);
        }

        if (!destination.getName().equals(source.getName())) {
            destination.setName(source.getName());
        }
        if (destination.getMinimumStock().compareTo(source.getMinimumStock()) != 0) {
            destination.setMinimumStock(source.getMinimumStock());
        }

        if (!destination.getCategory().getId().equals(source.getCategory().getId())) {
            Category newCategory = findInDatabaseAndValidate(source.getCategory());
            destination.setCategory(newCategory);
            newCategory.getIngredientChildren().add(destination);
        }

        if (destination.isActive() != source.isActive()) {
            if (source.isActive() && !destination.getCategory().isActive()) {
                throw new InvalidParentException(INACTIVE_CATEGORY);
            } else if (!source.isActive()) {
                if (destination.getProductDetails().stream().anyMatch(productDetail -> productDetail.getProduct().isActive())) {
                    throw new IngredientException(USED_INGREDIENT);
                }
            }
        }

        if (destination.isActive() && !destination.getCategory().isActive()) {
            throw new CategoryException(INACTIVE_CATEGORY);
        }
        return destination;

    }

    @Override
    public void beforeSaveValidations(Ingredient ingredient) {
        Category category = categoryService.findById(ingredient.getCategory().getId());
        if (category.getType().equals(CategoryType.PRODUCT)) {
            throw new CategoryException(NOT_FOR_INGREDIENTS_CATEGORY);
        }
        if (category.isContainer()) {
            throw new CategoryException(CONTAINER_CATEGORY);
        }
        if (!category.isActive()) {
            throw new CategoryException(INACTIVE_CATEGORY);
        }
    }

    private Category findInDatabaseAndValidate(Category source) {

        Category newCategoryDatabase = categoryService.findById(source.getId());

        if (!newCategoryDatabase.getType().equals(CategoryType.INGREDIENT)) {
            throw new CategoryException(INCOMPATIBLE_PARENT_CATEGORY_TYPE);
        }
        if (newCategoryDatabase.isContainer()) {
            throw new CategoryException(CONTAINER_CATEGORY);
        }
        return newCategoryDatabase;
    }

    @Override
    public Ingredient completeEntity(Ingredient ingredient) {
        addCurrentStock(ingredient);
        addLastCost(ingredient);
        return ingredient;
    }

    void addCurrentStock(Ingredient ingredient) {

        BigDecimal currentStock = BigDecimal.ZERO;

        if (Objects.nonNull(ingredient.getIngredientPurchaseList()) && ingredient.getIngredientPurchaseList().size() > 0) {
            currentStock = ingredient.getIngredientPurchaseList().stream()
                    .map(IngredientPurchase::getQuantity)
                    .reduce(currentStock, BigDecimal::add);
        }

        if (Objects.nonNull(ingredient.getProductDetails())) {
            currentStock = ingredient.getProductDetails().stream()
                    .map(pd -> {
                        int quantityOfProductOrdered = pd.getProduct().getOrderDetails().stream()
                                //TODO: Actualmente no resta el stock si la orden está cancelada, independientemente del estado que pudo haber tenido la orden
                                .filter(orderDetail -> !orderDetail.getOrder().getStatus().equals(Status.CANCELLED))
                                .mapToInt(OrderDetail::getQuantity)
                                .sum();
                        return pd.getQuantity().multiply(BigDecimal.valueOf(quantityOfProductOrdered));
                    })
                    .reduce(currentStock, BigDecimal::subtract);

        }
        ingredient.setCurrentStock(currentStock);
    }

    void addLastCost(Ingredient ingredient) {

        BigDecimal lastCost = BigDecimal.ZERO;

        if (Objects.nonNull(ingredient.getIngredientPurchaseList()) && ingredient.getIngredientPurchaseList().size() > 0) {
            lastCost = ingredient.getIngredientPurchaseList().stream()
                    .sorted(Comparator.comparing(IngredientPurchase::getDateTime).reversed())
                    .limit(1)
                    .map(IngredientPurchase::getUnitPrice)
                    .findFirst()
                    .get();
        }
        ingredient.setLastCost(lastCost);
    }
}
