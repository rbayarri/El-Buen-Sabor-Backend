package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ForgetPasswordTokenRepository extends JpaRepository<ForgetPasswordToken, UUID> {
}