package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.entities.Product;
import com.lacodigoneta.elbuensabor.entities.ProductDetail;
import com.lacodigoneta.elbuensabor.enums.CategoryType;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.exceptions.InvalidParentException;
import com.lacodigoneta.elbuensabor.exceptions.ProductException;
import com.lacodigoneta.elbuensabor.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lacodigoneta.elbuensabor.config.AppConstants.*;

@Service
@Slf4j
public class ProductService extends BaseServiceImpl<Product, ProductRepository> {

    private final CategoryService categoryService;

    private final ImageServiceFactory imageServiceFactory;

    private final ProductDetailService productDetailService;

    public ProductService(ProductRepository repository, CategoryService categoryService, ImageServiceFactory imageServiceFactory, ProductDetailService productDetailService) {
        super(repository);
        this.categoryService = categoryService;
        this.imageServiceFactory = imageServiceFactory;
        this.productDetailService = productDetailService;
    }

    public List<Product> findAllActive() {
        return repository.findAllByActiveTrue().stream().map(this::completeEntity).collect(Collectors.toList());
    }

    public List<Product> findAllByActiveAndCategory(String categoryName) {
        return repository.findAllByActiveTrueAndCategoryName(categoryName).stream().map(this::completeEntity).collect(Collectors.toList());
    }

    @Override
    public Product changeStates(Product source, Product destination) {

        if (!destination.getName().equals(source.getName())) {
            destination.setName(source.getName());
        }
        if (!destination.getDescription().equals(source.getDescription())) {
            destination.setDescription(source.getDescription());
        }
        if (!destination.getRecipe().equals(source.getRecipe())) {
            destination.setRecipe(source.getRecipe());
        }
        if (!destination.getCookingTime().equals(source.getCookingTime())) {
            destination.setCookingTime(source.getCookingTime());
        }

        if (!destination.getCategory().getId().equals(source.getCategory().getId())) {

            Category newCategoryDatabase = categoryService.findById(source.getId());
            validateCategoryTypeIngredient(newCategoryDatabase);
            validateCategoryIsContainer(newCategoryDatabase);

            destination.setCategory(newCategoryDatabase);
            newCategoryDatabase.getProductChildren().add(destination);
        }

        if (destination.isActive() != source.isActive()) {
            if (source.isActive()) {

                validateCategoryIsActive(destination.getCategory());

                if (destination.getProductDetails().stream().anyMatch(pd -> !pd.getIngredient().isActive())) {
                    throw new ProductException(INACTIVE_INGREDIENTS);
                }
            } else {
                if (destination.getOrderDetails().stream().anyMatch(od -> !od.getOrder().getStatus().equals(Status.READY))) {
                    throw new ProductException(USED_PRODUCT);
                }
            }
        }

        if (destination.isActive() && !destination.getCategory().isActive()) {
            throw new InvalidParentException(INACTIVE_CATEGORY);
        }

        source.getProductDetails().forEach(newProductDetail -> {
            Optional<ProductDetail> optionalExistingProductDetailCoincidence = destination.getProductDetails()
                    .stream()
                    .filter(existingProductDetail -> Objects.equals(existingProductDetail.getIngredient(), newProductDetail.getIngredient()))
                    .findFirst();
            optionalExistingProductDetailCoincidence.ifPresentOrElse(
                    existingProductDetailCoincidence -> {
                        if (!Objects.equals(existingProductDetailCoincidence, newProductDetail)) {
                            if (!existingProductDetailCoincidence.getClientMeasurementUnit().equals(newProductDetail.getClientMeasurementUnit())) {
                                existingProductDetailCoincidence.setClientMeasurementUnit(newProductDetail.getClientMeasurementUnit());
                            }
                            existingProductDetailCoincidence.setQuantity(newProductDetail.getQuantity());
                        }
                    },
                    () -> destination.getProductDetails().add(ProductDetail.builder()
                            .ingredient(newProductDetail.getIngredient())
                            .clientMeasurementUnit(newProductDetail.getClientMeasurementUnit())
                            .quantity(newProductDetail.getQuantity())
                            .product(destination)
                            .build())
            );
        });

        List<ProductDetail> result = destination.getProductDetails()
                .stream()
                .filter(pd -> !source.getProductDetails().contains(pd))
                .toList();

        result.forEach(notFoundPrductDetail -> {
            destination.getProductDetails().remove(notFoundPrductDetail);
        });

        return destination;

    }

    @Override
    public void beforeSaveValidations(Product product) {
        Category category = categoryService.findById(product.getCategory().getId());

        validateCategoryTypeIngredient(category);
        validateCategoryIsContainer(category);
        validateCategoryIsActive(category);

        //New save method not longer require this code
//        if (!Objects.isNull(product.getImage())) {
//            ImageService imageService = imageServiceFactory.getObject(false);
//            Image imageDatabase = imageService.findById(product.getImage().getId());
//            product.setImage(imageDatabase);
//        }
    }

    @Override
    public Product completeEntity(Product product) {
        addPrice(product);
        addStock(product);
        return product;
    }

    private void addPrice(Product product) {

        product.getProductDetails().forEach(pd -> {
            productDetailService.completeIngredient(pd.getIngredient());
        });

        BigDecimal price = BigDecimal.ZERO;
        product.getProductDetails().forEach(pd -> {
            price.add(pd.getIngredient().getLastCost().multiply(pd.getQuantity()));
        });

        product.setPrice(price.multiply(product.getProfitMargin()));
    }

    private void addStock(Product product) {

        int stock = product.getProductDetails().stream()
                .mapToInt(pd -> pd.getIngredient().getCurrentStock().divide(pd.getQuantity()).intValue())
                .min().getAsInt();

        product.setStock(stock);
    }

    private void validateCategoryIsActive(Category category) {
        if (!category.isActive()) {
            throw new InvalidParentException(INACTIVE_CATEGORY);
        }
    }

    private void validateCategoryIsContainer(Category category) {
        if (category.isContainer()) {
            throw new InvalidParentException(CONTAINER_CATEGORY);
        }
    }

    private void validateCategoryTypeIngredient(Category category) {
        if (!category.getType().equals(CategoryType.PRODUCT)) {
            throw new InvalidParentException(NOT_FOR_PRODUCTS_CATEGORY);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Product newSave(Product product, MultipartFile file, String url) {

        ImageService imageService = imageServiceFactory.getObject(Objects.nonNull(file));
        Image saved = imageService.save(Objects.nonNull(file) ? file : url);

        product.setImage(saved);
        beforeSaveValidations(product);
        return completeEntity(repository.save(product));
    }
}