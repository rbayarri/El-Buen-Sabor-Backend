package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.IngredientPurchaseDto;
import com.lacodigoneta.elbuensabor.entities.IngredientPurchase;
import com.lacodigoneta.elbuensabor.mappers.IngredientPurchaseMapper;
import com.lacodigoneta.elbuensabor.services.IngredientPurchaseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/ingredients/purchase")
@AllArgsConstructor
public class IngredientPurchaseController {

    private final IngredientPurchaseService service;

    private final IngredientPurchaseMapper mapper;

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<IngredientPurchaseDto> save(@RequestBody @Valid IngredientPurchaseDto ingredientPurchaseDto) {

        IngredientPurchase saved = service.save(mapper.toEntity(ingredientPurchaseDto));
        IngredientPurchaseDto savedIngredientPurchaseDto = mapper.toDto(saved);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedIngredientPurchaseDto.getId())
                                .toUri())
                .body(savedIngredientPurchaseDto);
    }


}
