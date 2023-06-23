package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationResponse;
import com.lacodigoneta.elbuensabor.dto.auth.RegistrationRequest;
import com.lacodigoneta.elbuensabor.dto.user.NewPassword;
import com.lacodigoneta.elbuensabor.dto.user.ProfileUserDto;
import com.lacodigoneta.elbuensabor.dto.user.UpdateUser;
import com.lacodigoneta.elbuensabor.dto.user.UserByAdminDto;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.entities.VerifyEmailToken;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.services.JavaMailService;
import com.lacodigoneta.elbuensabor.services.UserService;
import com.lacodigoneta.elbuensabor.services.VerifyEmailTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    private final UserMapper mapper;

    private final VerifyEmailTokenService verifyEmailTokenService;

    private final JavaMailService mailService;

    @GetMapping(value = "", params = "pageable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserByAdminDto>> findAll(Pageable pageable) {
        Page<User> allPaged = service.findAllPaged(pageable);
        return ResponseEntity.ok(allPaged.map(mapper::toUserByAdminDto));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserByAdminDto>> findAll() {
        List<User> users = service.findAll();
        return ResponseEntity.ok(users.stream().map(mapper::toUserByAdminDto).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserByAdminDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toUserByAdminDto(service.findById(id)));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<ProfileUserDto> getProfile() {
        return ResponseEntity.ok(service.getProfileInformation());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserByAdminDto> save(@RequestBody @Valid RegistrationRequest request) {

        User saved = service.save(mapper.toEntity(request));
        return ResponseEntity.ok(mapper.toUserByAdminDto(saved));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<UserByAdminDto> update(@PathVariable UUID id,
                                                 @RequestBody @Valid UpdateUser updateUser) {
        User user = service.update(id, mapper.toEntity(updateUser));
        return ResponseEntity.ok(mapper.toUserByAdminDto(user));
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
    public ResponseEntity<AuthenticationResponse> updatePassword(@RequestBody @Valid NewPassword newPassword) {
        return ResponseEntity.ok(service.updatePassword(newPassword));
    }

    @PostMapping("/verifyEmail")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CASHIER', 'ROLE_CHEF', 'ROLE_DELIVERY')")
    public ResponseEntity<?> sendEmailVerification() {
        User user = service.getLoggedUser();
        VerifyEmailToken token = new VerifyEmailToken(user, LocalDateTime.now().plusDays(1));
        VerifyEmailToken savedToken = verifyEmailTokenService.save(token);

        mailService.sendHtml("lacodigoneta@gmail.com", user.getUsername(), "Verificación de correo electrónico",
                "<p>Por favor haga click en el siguiente enlace para verificar su email</p>" +
                        "<a href='" + ORIGIN_APP +"/verifyEmail/" + user.getId() + "/" + savedToken.getId() + "'>" +
                        ORIGIN_APP +"/verifyEmail/" + user.getId() + "/" + savedToken.getId() + "</a>"
        );
        return ResponseEntity.ok("Email de verificación enviado");
    }
}