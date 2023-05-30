package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.IngredientPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IngredientPurchaseRepository extends JpaRepository<IngredientPurchase, UUID> {

    List<IngredientPurchase> findAllByIngredientIdAndDateTimeAfter(UUID id, LocalDateTime dateTime);

}