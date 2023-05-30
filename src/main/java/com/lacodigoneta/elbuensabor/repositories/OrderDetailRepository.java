package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
}