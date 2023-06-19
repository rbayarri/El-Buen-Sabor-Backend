package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Profit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProfitRepository extends JpaRepository<Profit, UUID> {

    List<Profit> findAllByDateTimeBetween(LocalDateTime from, LocalDateTime to);
}
