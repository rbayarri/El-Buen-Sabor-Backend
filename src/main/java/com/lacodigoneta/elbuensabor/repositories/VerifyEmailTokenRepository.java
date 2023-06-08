package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.VerifyEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken, UUID> {
}