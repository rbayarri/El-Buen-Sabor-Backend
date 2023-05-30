package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Category;
import com.lacodigoneta.elbuensabor.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByParentIsNullAndTypeEquals(CategoryType categoryType);

    // PARA CARGAR RUBROS
    List<Category> findAllByContainerTrueAndActiveTrueAndTypeEquals(CategoryType categoryType);

    // PARA CARGAR INGREDIENTES O PRODUCTOS
    List<Category> findAllByContainerFalseAndActiveTrueAndTypeEquals(CategoryType categoryType);
}
