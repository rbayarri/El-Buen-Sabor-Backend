package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.Product;
import com.lacodigoneta.elbuensabor.enums.CategoryType;
import com.lacodigoneta.elbuensabor.exceptions.CategoryException;
import com.lacodigoneta.elbuensabor.exceptions.InvalidParentException;
import com.lacodigoneta.elbuensabor.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Service
public class CategoryService extends BaseServiceImpl<Category, CategoryRepository> {


    public CategoryService(CategoryRepository repository) {
        super(repository);
    }

    public List<Category> findProductCategoryRoots() {
        return repository.findAllByParentIsNullAndTypeEquals(CategoryType.PRODUCT);
    }

    public List<Category> findIngredientCategoryRoots() {
        return repository.findAllByParentIsNullAndTypeEquals(CategoryType.INGREDIENT);
    }

    public List<Category> findForContainerUseAndActiveCategories(CategoryType categoryType) {
        return repository.findAllByContainerTrueAndActiveTrueAndTypeEquals(categoryType);
    }

    public List<Category> findForContainerUseFalseAndActiveForProductsCategories() {
        return repository.findAllByContainerFalseAndActiveTrueAndTypeEquals(CategoryType.PRODUCT);
    }

    public List<Category> findForContainerUseFalseAndActiveForIngredientCategories() {
        return repository.findAllByContainerFalseAndActiveTrueAndTypeEquals(CategoryType.INGREDIENT);
    }

    @Override
    public void beforeSaveValidations(Category category) {

        if (!Objects.isNull(category.getParent())) {
            Category parent = findInDatabaseAndDoValidations(category, category.getParent());
            category.setParent(parent);
            checkConsistency(category);
        }

    }

    @Override
    public Category changeStates(Category source, Category destination) {

        if (!source.getName().equals(destination.getName())) {
            destination.setName(source.getName());
        }

        changeContainer(source, destination);
        changeParent(source, destination);
        changeActive(source, destination);
        checkConsistency(destination);
        return destination;
    }

    private void changeContainer(Category source, Category destination) {
        if (source.isContainer() != destination.isContainer()) {

            if (hasChilden(destination)) {
                throw new CategoryException(CATEGORY_WITH_CHILDREN);
            }
            destination.setContainer(source.isContainer());
        }
    }

    private void changeParent(Category source, Category destination) {

        Category newParentByUser = source.getParent();
        Category databaseParent = destination.getParent();

        if (Objects.isNull(newParentByUser) && !Objects.isNull(databaseParent)) {
            destination.setParent(null);

        } else if (!Objects.isNull(newParentByUser) && Objects.isNull(databaseParent)) {

            Category parentById = findInDatabaseAndDoValidations(destination, newParentByUser);

            destination.setParent(parentById);
            parentById.getSubCategories().add(destination);

        } else if (!Objects.isNull(newParentByUser)) {

            if (!newParentByUser.getId().equals(databaseParent.getId())) {

                Category parentById = findInDatabaseAndDoValidations(destination, newParentByUser);
                destination.setParent(parentById);
                parentById.getSubCategories().add(destination);
            }
        }
    }

    private Category findInDatabaseAndDoValidations(Category destination, Category newParentByUser) {

        Category parentById = findById(newParentByUser.getId());

        if (!parentById.getType().equals(destination.getType())) {
            throw new InvalidParentException(INCOMPATIBLE_PARENT_CATEGORY_TYPE);
        }
        if (!parentById.isContainer()) {
            throw new InvalidParentException(NOT_CONTAINER_PARENT_CATEGORY);
        }

        if (hasChildWithId(destination, newParentByUser.getId())) {
            throw new InvalidParentException(CHILD_PARENT_CATEGORY);
        }

        return parentById;
    }

    private void changeActive(Category source, Category destination) {

        if (source.isActive() != destination.isActive()) {
            if (source.isActive()) {
                if (!Objects.isNull(destination.getParent())) {
                    if (!destination.getParent().isActive()) {
                        throw new InvalidParentException(INACTIVE_PARENT_CATEGORY);
                    }
                }
                destination.setActive(true);
            } else {
                if (hasActiveChildren(destination)) {
                    throw new CategoryException(ACTIVE_CHILDREN_CATEGORY);
                }
                destination.setActive(false);
            }
        }
    }

    private boolean hasActiveChildren(Category category) {

        if (category.isContainer()) {
            List<Category> subCategories = category.getSubCategories();
            if (!Objects.isNull(subCategories) && subCategories.size() != 0) {
                return subCategories.stream().anyMatch(Category::isActive) || subCategories.stream().anyMatch(this::hasActiveChildren);
            }
        } else {
            if (category.getType().equals(CategoryType.INGREDIENT)) {
                List<Ingredient> ingredientChildren = category.getIngredientChildren();
                if (!Objects.isNull(ingredientChildren) && ingredientChildren.size() != 0) {
                    return ingredientChildren.stream().anyMatch(Ingredient::isActive);
                }
            } else {
                List<Product> productChildren = category.getProductChildren();
                if (!Objects.isNull(productChildren) && productChildren.size() != 0) {
                    return productChildren.stream().anyMatch(Product::isActive);
                }
            }
        }
        return false;
    }

    private boolean hasChilden(Category category) {
        if (category.isContainer()) {
            return !Objects.isNull(category.getSubCategories()) && category.getSubCategories().size() != 0;
        } else {
            if (category.getType().equals(CategoryType.INGREDIENT)) {
                return !Objects.isNull(category.getIngredientChildren()) && category.getIngredientChildren().size() != 0;
            } else {
                return !Objects.isNull(category.getProductChildren()) && category.getProductChildren().size() != 0;
            }
        }
    }

    private boolean hasChildWithId(Category category, UUID id) {
        if (!Objects.isNull(category.getSubCategories()) && category.getSubCategories().size() != 0) {
            return category.getSubCategories().stream().anyMatch(cat -> cat.getId().equals(id)) || category.getSubCategories().stream().anyMatch(cat -> hasChildWithId(cat, id));
        }
        return false;
    }

    private void checkConsistency(Category destination) {

        if (!Objects.isNull(destination.getParent())) {
            if (destination.isActive() && !destination.getParent().isActive()) {
                throw new InvalidParentException(INACTIVE_PARENT_CATEGORY);
            }
        }
    }

    public List<Product> findActiveProductsByCategoryRoot(Category category) {
        if (!category.getType().equals(CategoryType.PRODUCT)) {
            throw new CategoryException(NOT_FOR_PRODUCTS_CATEGORY);
        }
        if (!category.isActive()) {
            throw new CategoryException(INACTIVE_CATEGORY);
        }
        return getActiveProducts(category);
    }

    private List<Product> getActiveProducts(Category category) {
        List<Product> productList = new ArrayList<>();
        if (category.isContainer()) {
            for (Category subCategory : category.getSubCategories()) {
                productList.addAll(getActiveProducts(subCategory));
            }
        } else {
            productList.addAll(category.getProductChildren().stream().filter(Product::isActive).toList());
        }
        return productList;
    }

}
