package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.product.ClientProductDto;
import com.lacodigoneta.elbuensabor.dto.product.ProductDto;
import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.entities.Product;
import com.lacodigoneta.elbuensabor.mappers.ProductMapper;
import com.lacodigoneta.elbuensabor.services.CategoryService;
import com.lacodigoneta.elbuensabor.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Validated
public class ProductController {

    private ProductService service;

    private ProductMapper mapper;

    private CategoryService categoryService;


    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_CHEF', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProductDto>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toProductDto).collect(Collectors.toList()));
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ClientProductDto> findByIdClient(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toClientProductDto(service.findById(id)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CHEF', 'ROLE_ADMIN')")
    public ResponseEntity<ProductDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toProductDto(service.findById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClientProductDto>> findAllActive() {
        return ResponseEntity.ok(service.findAllActive().stream().map(mapper::toClientProductDto).collect(Collectors.toList()));
    }

    @GetMapping(value = "/active", params = "name")
    public ResponseEntity<List<ClientProductDto>> findAllActiveByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findAllActiveByName(name).stream().map(mapper::toClientProductDto).collect(Collectors.toList()));
    }

    @GetMapping(value = "/active/category/{id}")
    public ResponseEntity<List<ClientProductDto>> findAllActiveProductsByActiveCategories(@PathVariable UUID id) {
        Category category = categoryService.findById(id);
        List<Product> activeProductsByCategoryRoot = service.findActiveProductsByCategoryRoot(category);
        return ResponseEntity.ok(activeProductsByCategoryRoot.stream().map(mapper::toClientProductDto).toList());
    }

    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<ProductDto> save(@RequestPart("product") @Valid ProductDto productDto,
                                           @RequestPart(value = "image", required = false) MultipartFile file,
                                           @RequestPart(value = "imageUrl", required = false) String url) {

        Product saved = service.save(mapper.toEntity(productDto), file, url);
        ProductDto savedProductDto = mapper.toProductDto(saved);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                                .path("/{id}")
                                .buildAndExpand(savedProductDto.getId())
                                .toUri())
                .body(savedProductDto);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHEF')")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id,
                                             @RequestPart("product") @Valid ProductDto productDto,
                                             @RequestPart(value = "image", required = false) MultipartFile file,
                                             @RequestPart(value = "imageUrl", required = false) String url) {
        Product updated = service.update(id, mapper.toEntity(productDto), file, url);
        ProductDto updatedProductDto = mapper.toProductDto(updated);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                                .build()
                                .toUri())
                .body(updatedProductDto);
    }
}
