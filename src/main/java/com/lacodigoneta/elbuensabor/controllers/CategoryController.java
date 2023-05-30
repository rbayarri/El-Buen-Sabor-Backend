package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.category.CategoryDto;
import com.lacodigoneta.elbuensabor.dto.category.CategoryWithSubcategoriesDto;
import com.lacodigoneta.elbuensabor.dto.category.NewEditCategoryDto;
import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.enums.CategoryType;
import com.lacodigoneta.elbuensabor.mappers.CategoryMapper;
import com.lacodigoneta.elbuensabor.services.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;

    private final CategoryMapper mapper;


    @GetMapping("/{id}")
    public ResponseEntity<NewEditCategoryDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toNewEditCategoryDto(service.findById(id)));
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<CategoryWithSubcategoriesDto>> findAllIngredientCategoryRoots() {
        return ResponseEntity.ok(service.findIngredientCategoryRoots().stream().map(mapper::toCategoryWithSubcategoriesDto).collect(Collectors.toList()));
    }

    @GetMapping("/products")
    public ResponseEntity<List<CategoryWithSubcategoriesDto>> findAllProductCategoryRoots() {
        return ResponseEntity.ok(service.findProductCategoryRoots().stream().map(mapper::toCategoryWithSubcategoriesDto).collect(Collectors.toList()));
    }

    @GetMapping("/products/active")
    public ResponseEntity<List<CategoryWithSubcategoriesDto>> findAllActiveProductCategoryRoots() {
        List<CategoryWithSubcategoriesDto> rootCategories = service.findProductCategoryRoots().stream().map(mapper::toCategoryWithSubcategoriesDto).collect(Collectors.toList());
        return ResponseEntity.ok(filterInactiveCategories(rootCategories));
    }

    @GetMapping("/ingredients/container/active")
    public ResponseEntity<List<CategoryDto>> findAllIngredientCategoriesForContainerUseAndActive() {
        return ResponseEntity.ok(service.findForContainerUseAndActiveCategories(CategoryType.INGREDIENT).stream().map(mapper::toCategoryDto).collect(Collectors.toList()));
    }

    @GetMapping("/products/container/active")
    public ResponseEntity<List<CategoryDto>> findAllProductCategoriesForContainerUseAndActive() {
        return ResponseEntity.ok(service.findForContainerUseAndActiveCategories(CategoryType.PRODUCT).stream().map(mapper::toCategoryDto).collect(Collectors.toList()));
    }

    @GetMapping("/products/final/active")
    public ResponseEntity<List<CategoryDto>> findFinalActiveProductsCategories() {
        return ResponseEntity.ok(service.findForContainerUseFalseAndActiveForProductsCategories().stream().map(mapper::toCategoryDto).collect(Collectors.toList()));
    }

    @GetMapping("/ingredients/final/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<List<CategoryDto>> findFinalActiveIngredientsCategories() {
        return ResponseEntity.ok(service.findForContainerUseFalseAndActiveForIngredientCategories().stream().map(mapper::toCategoryDto).collect(Collectors.toList()));
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<NewEditCategoryDto> saveProductCategory(@RequestBody @Valid NewEditCategoryDto newEditCategoryDto) {
        return save(newEditCategoryDto, CategoryType.PRODUCT);
    }

    @PostMapping("/ingredients")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<NewEditCategoryDto> saveIngredientCategory(@RequestBody @Valid NewEditCategoryDto newEditCategoryDto) {
        return save(newEditCategoryDto, CategoryType.INGREDIENT);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<NewEditCategoryDto> update(@PathVariable UUID id, @RequestBody @Valid NewEditCategoryDto newEditCategoryDto) {
        Category savedCategory = service.update(id, mapper.toEntity(newEditCategoryDto));
        NewEditCategoryDto mappedSavedCategory = mapper.toNewEditCategoryDto(savedCategory);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .build()
                        .toUri())
                .body(mappedSavedCategory);
    }

    private ResponseEntity<NewEditCategoryDto> save(NewEditCategoryDto newEditCategoryDto, CategoryType categoryType) {
        Category category = mapper.toEntity(newEditCategoryDto);
        category.setType(categoryType);
        Category savedCategory = service.save(category);
        NewEditCategoryDto savedNewEditCategoryDto = mapper.toNewEditCategoryDto(savedCategory);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .path("/{id}")
                        .buildAndExpand(savedCategory.getId())
                        .toUri())
                .body(savedNewEditCategoryDto);
    }

    private List<CategoryWithSubcategoriesDto> filterInactiveCategories(List<CategoryWithSubcategoriesDto> categories) {

        List<CategoryWithSubcategoriesDto> activeCategories = categories.stream().filter(CategoryWithSubcategoriesDto::isActive).toList();

        activeCategories.forEach(cat -> filterInactiveCategories(cat.getSubCategories()));

        return activeCategories;

    }

}
