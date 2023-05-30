package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.dto.category.CategoryWithSubcategoriesDto;
import com.lacodigoneta.elbuensabor.dto.category.NewEditCategoryDto;
import com.lacodigoneta.elbuensabor.entities.Category;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CategoryMapper {

    private final ModelMapper mapper;

    public CategoryWithSubcategoriesDto toCategoryWithSubcategoriesDto(Category category) {
        return mapper.map(category, CategoryWithSubcategoriesDto.class);
    }

    public CategoryDto toCategoryDto(Category category) {
        return mapper.map(category, CategoryDto.class);
    }

    public NewEditCategoryDto toNewEditCategoryDto(Category category) {
        NewEditCategoryDto newEditCategoryDto = mapper.map(category, NewEditCategoryDto.class);
        if (Objects.nonNull(category.getParent())) {
            newEditCategoryDto.setParent(toCategoryDto(category.getParent()));
        }
        return newEditCategoryDto;
    }

    public Category toEntity(NewEditCategoryDto newEditCategoryDto) {
        Category category = mapper.map(newEditCategoryDto, Category.class);
        if (Objects.nonNull(newEditCategoryDto.getParent())) {
            category.setParent(toEntity(newEditCategoryDto.getParent()));
        }
        return category;
    }

    public Category toEntity(CategoryDto categoryDto) {
        return mapper.map(categoryDto, Category.class);
    }
}
