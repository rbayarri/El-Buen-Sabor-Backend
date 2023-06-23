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
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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

    public List<Product> findAllActiveByName(String name) {
        return repository.findAllByNameContainingIgnoreCaseAndActiveTrue(name).stream().map(this::completeEntity).collect(Collectors.toList());
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
        if (Objects.nonNull(destination.getRecipe())) {
            if (!destination.getRecipe().equals(source.getRecipe())) {
                destination.setRecipe(source.getRecipe());
            }
        } else if (Objects.nonNull(source.getRecipe())) {
            destination.setRecipe(source.getRecipe());
        }

        if (!destination.getCookingTime().equals(source.getCookingTime())) {
            destination.setCookingTime(source.getCookingTime());
        }

        if (!destination.getProfitMargin().equals(source.getProfitMargin())) {
            destination.setProfitMargin(source.getProfitMargin());
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

//        final BigDecimal[] price = {BigDecimal.ZERO};
//        product.getProductDetails().forEach(pd -> {
//            price[0] = price[0].add(pd.getIngredient().getLastCost().multiply(pd.getQuantity()));
//        });
//
//        product.setPrice(price[0].multiply(product.getProfitMargin().add(BigDecimal.ONE)));

        BigDecimal price = BigDecimal.ZERO;
        price = product.getProductDetails().stream()
                .map(pd -> pd.getIngredient().getLastCost().multiply(pd.getQuantity()))
                .reduce(price, BigDecimal::add);

        product.setPrice(price.multiply(product.getProfitMargin().add(BigDecimal.ONE)));
    }

    private void addStock(Product product) {

        int stock = product.getProductDetails().stream()
                .mapToInt(pd -> {
                    if (pd.getIngredient().getCurrentStock().equals(BigDecimal.ZERO)) {
                        return 0;
                    } else {
                        return pd.getIngredient().getCurrentStock().divide(pd.getQuantity(), 10, RoundingMode.UP).intValue();
                    }
                })
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
    public Product save(Product product, MultipartFile file, String url) {

        boolean hasFile = Objects.nonNull(file);
        boolean hasUrl = Objects.nonNull(url);

        if (hasFile || hasUrl) {
            saveImage(hasFile, hasFile ? file : url, product);
        } else {
            saveImage(false, "https://www.webempresa.com/foro/wp-content/uploads/wpforo/attachments/3200/318277=80538-Sin_imagen_disponible.jpg", product);
        }
        beforeSaveValidations(product);
        return completeEntity(repository.save(product));
    }

    @Transactional(rollbackOn = Exception.class)
    public Product update(UUID id, Product product, MultipartFile file, String url) {

        Product byId = findById(id);

        boolean hasFile = Objects.nonNull(file);
        boolean hasUrl = Objects.nonNull(url);

        if (hasFile || (hasUrl && !byId.getImage().getLocation().equals(url))) {
            saveImage(hasFile, hasFile ? file : url, byId);
        } else if (!hasUrl) {
            saveImage(false, "https://www.webempresa.com/foro/wp-content/uploads/wpforo/attachments/3200/318277=80538-Sin_imagen_disponible.jpg", byId);
        }

        changeStates(product, byId);
        return completeEntity(byId);
    }

    private void saveImage(boolean hasFile, Object image, Product byId) {
        ImageService imageService = imageServiceFactory.getObject(hasFile);
        Image saved = imageService.save(image);
        byId.setImage(saved);
    }

    public List<Product> findActiveProductsByCategoryRoot(Category category) {
        return categoryService.findActiveProductsByCategoryRoot(category).stream()
                .map(this::completeEntity)
                .toList();
    }
}