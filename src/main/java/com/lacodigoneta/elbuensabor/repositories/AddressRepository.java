package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findAllByUserUsername(String username);

    List<Address> findAllByActiveTrueAndUserUsername(String username);

}