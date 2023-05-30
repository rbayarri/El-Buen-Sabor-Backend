package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.dto.ingredient.IngredientDto;
import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IngredientMapper {

    private final ModelMapper mapper;

    public IngredientDto toIngredientDto(Ingredient ingredient) {
        IngredientDto ingredientDto = mapper.map(ingredient, IngredientDto.class);
        CategoryDto categoryDto = mapper.map(ingredient.getCategory(), CategoryDto.class);
        ingredientDto.setCategory(categoryDto);
        return ingredientDto;
    }

    public Ingredient toEntity(IngredientDto ingredientDto) {

        Ingredient ingredient = mapper.map(ingredientDto, Ingredient.class);
        Category category = mapper.map(ingredientDto.getCategory(), Category.class);
        ingredient.setCategory(category);
        return ingredient;
    }
}
