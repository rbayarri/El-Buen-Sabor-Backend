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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<ProfileUserDto> update(@RequestPart("user") @Valid UpdateUser updateUser,
                                                 @RequestPart(value = "image", required = false) MultipartFile file,
                                                 @RequestPart(value = "imageUrl", required = false) String url) {

        User user = service.updateUser(updateUser, file, url);
        return ResponseEntity.ok(mapper.toProfileUserDto(user));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ProfileUserDto> update(@PathVariable UUID id,
                                                 @RequestPart("user") @Valid UpdateUser updateUser,
                                                 @RequestPart(value = "image", required = false) MultipartFile file,
                                                 @RequestPart(value = "imageUrl", required = false) String url) {
        User user = service.updateUser(id, updateUser, file, url);
        return ResponseEntity.ok(mapper.toProfileUserDto(user));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid NewPassword newPassword) {
        return ResponseEntity.ok(service.updatePassword(newPassword));
    }

}