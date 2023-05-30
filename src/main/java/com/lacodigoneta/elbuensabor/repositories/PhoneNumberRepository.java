package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, UUID> {

    List<PhoneNumber> findAllByUserUsername(String username);

    List<PhoneNumber> findAllByActiveTrueAndUserUsername(String username);
}