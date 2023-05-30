package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Profit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfilRepository extends JpaRepository<Profit, UUID> {
}
