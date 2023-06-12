package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditNoteRepository extends JpaRepository<CreditNote, UUID> {
}