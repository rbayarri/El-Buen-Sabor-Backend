package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationRequest;
import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationResponse;
import com.lacodigoneta.elbuensabor.dto.auth.GoogleAuthentication;
import com.lacodigoneta.elbuensabor.dto.auth.RegistrationRequest;
import com.lacodigoneta.elbuensabor.dto.user.CreatedUser;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.services.JwtService;
import com.lacodigoneta.elbuensabor.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService service;

    private final JwtService jwtService;

    private final UserMapper mapper;

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(service.authenticate(authenticationRequest));
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {

        User saved = service.save(mapper.toEntity(registrationRequest));
        return ResponseEntity.ok(new AuthenticationResponse(jwtService.createToken(saved, false)));

    }

    @PostMapping("/googleLoginSignup")
    public ResponseEntity<AuthenticationResponse> registerWithGoogle(@RequestBody @Valid GoogleAuthentication googleAuthentication) throws GeneralSecurityException, IOException {

        User userFromGoogleToken = jwtService.getUserFromGoogleToken(googleAuthentication.getGoogleToken());
        AuthenticationResponse authenticationResponse = service.authenticateFromGoogle(userFromGoogleToken);

        return ResponseEntity.ok(authenticationResponse);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/registration")
    public ResponseEntity<CreatedUser> registerByAdmin(@RequestBody @Valid RegistrationRequest registrationRequest) {
        User saved = service.save(mapper.toEntity(registrationRequest));
        CreatedUser createdUser = mapper.toCreatedUser(saved);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                                .path("/{id}")
                                .buildAndExpand(createdUser.getId())
                                .toUri())
                .body(createdUser);
    }

}
