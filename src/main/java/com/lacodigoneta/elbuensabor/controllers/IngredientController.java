package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.ingredient.IngredientDto;
import com.lacodigoneta.elbuensabor.dto.ingredient.UpdateIngredientCost;
import com.lacodigoneta.elbuensabor.dto.ingredient.UpdateIngredientStock;
import com.lacodigoneta.elbuensabor.entities.Ingredient;
import com.lacodigoneta.elbuensabor.mappers.IngredientMapper;
import com.lacodigoneta.elbuensabor.services.IngredientPurchaseService;
import com.lacodigoneta.elbuensabor.services.IngredientService;
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
@RequestMapping("/api/v1/ingredients")
@AllArgsConstructor
public class IngredientController {

    private final IngredientService service;

    private final IngredientMapper mapper;

    private final IngredientPurchaseService ingredientPurchaseService;

    @GetMapping("")
    public ResponseEntity<List<IngredientDto>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toIngredientDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toIngredientDto(service.findById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<IngredientDto>> findAllActive() {
        return ResponseEntity.ok(service.findAllByActive().stream().map(mapper::toIngredientDto).collect(Collectors.toList()));
    }

    @PostMapping("/updateCost/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<IngredientDto> updateCost(@PathVariable UUID id, @RequestBody @Valid UpdateIngredientCost updateIngredientCost) {
        Ingredient ingredient = ingredientPurchaseService.updateCost(id, updateIngredientCost);
        return ResponseEntity.ok(mapper.toIngredientDto(ingredient));
    }

    @PostMapping("/updateStock/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<IngredientDto> updateStock(@PathVariable UUID id, @RequestBody @Valid UpdateIngredientStock updateIngredientStock) {
        Ingredient ingredient = ingredientPurchaseService.updateStock(id, updateIngredientStock);
        return ResponseEntity.ok(mapper.toIngredientDto(ingredient));
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<IngredientDto> save(@RequestBody @Valid IngredientDto ingredientDto) {
        Ingredient saved = service.save(mapper.toEntity(ingredientDto));
        IngredientDto mappedIngredient = mapper.toIngredientDto(saved);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                                .path("{id}")
                                .buildAndExpand(mappedIngredient.getId())
                                .toUri())
                .body(mappedIngredient);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<IngredientDto> update(@PathVariable UUID id, @RequestBody @Valid IngredientDto ingredientDto) {

        Ingredient updated = service.update(id, mapper.toEntity(ingredientDto));
        IngredientDto updatedIngredientDto = mapper.toIngredientDto(updated);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                                .build()
                                .toUri())
                .body(updatedIngredientDto);
    }
}
