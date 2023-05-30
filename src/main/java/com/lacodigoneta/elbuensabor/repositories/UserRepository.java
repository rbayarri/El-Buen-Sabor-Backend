package com.lacodigoneta.elbuensabor.repositories;

import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findUserByUsernameAndActiveTrue(String username);

    int countUsersByRoleAndActiveTrue(Role role);

}