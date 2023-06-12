package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}