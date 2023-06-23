package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.auth.*;
import com.lacodigoneta.elbuensabor.dto.user.CreatedUser;
import com.lacodigoneta.elbuensabor.dto.user.NewPassword;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.entities.VerifyEmailToken;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.services.JavaMailService;
import com.lacodigoneta.elbuensabor.services.JwtService;
import com.lacodigoneta.elbuensabor.services.UserService;
import com.lacodigoneta.elbuensabor.services.VerifyEmailTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService service;

    private final JwtService jwtService;

    private final UserMapper mapper;

    private final JavaMailService mailService;

    private final VerifyEmailTokenService verifyEmailTokenService;

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(service.authenticate(authenticationRequest));
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {

        User saved = service.save(mapper.toEntity(registrationRequest));
        VerifyEmailToken token = new VerifyEmailToken(saved, LocalDateTime.now().plusDays(1));
        VerifyEmailToken savedToken = verifyEmailTokenService.save(token);

        mailService.sendHtml("lacodigoneta@gmail.com", saved.getUsername(), "¡Bienvenido!",
                "<p>Gracias por registrarte en nuestro sitio web</p>" +
                        "<p>Por favor haga click en el siguiente enlace para verificar su email</p>" +
                        "<a href='" + ORIGIN_APP + "/verifyEmail/" + saved.getId() + "/" + savedToken.getId() + "'>" +
                        ORIGIN_APP + "/verifyEmail/" + saved.getId() + "/" + savedToken.getId() + "</a>"
        );

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

    @PostMapping("/verifyEmail/{userId}/{tokenId}")
    public ResponseEntity<?> verifyEmail(@PathVariable UUID userId,
                                         @PathVariable UUID tokenId) {
        service.validateEmail(userId, tokenId);
        return ResponseEntity.ok("Email verificado");
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request) {
        service.forgetPassword(request);
        return ResponseEntity.ok(new BasicResponse("Correo Enviado"));
    }

    @PostMapping("/verifyForgetPasswordToken/{userId}/{tokenId}")
    public ResponseEntity<?> verifyForgetPasswordToken(@PathVariable UUID userId,
                                                       @PathVariable UUID tokenId) {
        service.validateForgetPasswordToken(userId, tokenId);
        return ResponseEntity.ok("Token válido");
    }

    @PostMapping("/resetPassword/{userId}/{tokenId}")
    public ResponseEntity<?> resetPassword(@PathVariable UUID userId,
                                           @PathVariable UUID tokenId,
                                           @RequestBody @Valid NewPassword request) {
        return ResponseEntity.ok(service.resetPassword(userId, tokenId, request));
    }
}
