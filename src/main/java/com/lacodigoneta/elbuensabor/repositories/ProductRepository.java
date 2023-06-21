package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByActiveTrue();

    List<Product> findAllByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Product> findAllByActiveTrueAndCategoryName(String categoryName);
}