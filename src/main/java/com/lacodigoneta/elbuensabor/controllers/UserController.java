package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.user.NewPassword;
import com.lacodigoneta.elbuensabor.dto.user.ProfileUserDto;
import com.lacodigoneta.elbuensabor.dto.user.UpdateUser;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    private final UserMapper mapper;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<User>> findAll(Pageable pageable) {
        Page<User> allPaged = service.findAllPaged(pageable);
        return ResponseEntity.ok(allPaged);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> findAll(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<ProfileUserDto> getProfile() {
        return ResponseEntity.ok(service.getProfileInformation());
    }

    @PutMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<ProfileUserDto> update(@RequestBody @Valid UpdateUser updateUser) {
        User user = service.updateUser(updateUser);
        return ResponseEntity.ok(mapper.toProfileUserDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ProfileUserDto> update(@PathVariable UUID id,
                                                 @RequestBody @Valid UpdateUser updateUser) {
        User user = service.updateUser(id, updateUser);
        return ResponseEntity.ok(mapper.toProfileUserDto(user));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid NewPassword newPassword) {
        return ResponseEntity.ok(service.updatePassword(newPassword));
    }

}