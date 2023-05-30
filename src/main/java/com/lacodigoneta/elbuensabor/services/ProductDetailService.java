package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.entities.ProductDetail;
import com.lacodigoneta.elbuensabor.repositories.ProductDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailService extends BaseServiceImpl<ProductDetail, ProductDetailRepository> {

    private final IngredientService ingredientService;


    public ProductDetailService(ProductDetailRepository repository,
                                IngredientService ingredientService) {
        super(repository);
        this.ingredientService = ingredientService;
    }

    @Override
    public ProductDetail changeStates(ProductDetail source, ProductDetail destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(ProductDetail entity) {
    }

    public Ingredient completeIngredient(Ingredient ingredient) {
        return ingredientService.completeEntity(ingredient);
    }

}
