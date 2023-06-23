package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationRequest;
import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationResponse;
import com.lacodigoneta.elbuensabor.dto.auth.ForgetPasswordRequest;
import com.lacodigoneta.elbuensabor.dto.user.NewPassword;
import com.lacodigoneta.elbuensabor.dto.user.ProfileUserDto;
import com.lacodigoneta.elbuensabor.dto.user.UpdateUser;
import com.lacodigoneta.elbuensabor.entities.ForgetPasswordToken;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.entities.VerifyEmailToken;
import com.lacodigoneta.elbuensabor.enums.Role;
import com.lacodigoneta.elbuensabor.exceptions.InvalidCredentialsException;
import com.lacodigoneta.elbuensabor.exceptions.NoLoggedUserException;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.ORIGIN_APP;

@Service
public class UserService extends BaseServiceImpl<User, UserRepository> {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final ImageUrlService imageUrlService;

    private final UserMapper mapper;

    private final VerifyEmailTokenService verifyEmailTokenService;

    private final ForgetPasswordTokenService forgetPasswordTokenService;

    private final JavaMailService mailService;

    private final ImageServiceFactory imageServiceFactory;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, ImageUrlService imageUrlService, UserMapper mapper, VerifyEmailTokenService verifyEmailTokenService, ForgetPasswordTokenService forgetPasswordTokenService, JavaMailService mailService, ImageServiceFactory imageServiceFactory) {
        super(repository);
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.imageUrlService = imageUrlService;
        this.mapper = mapper;
        this.verifyEmailTokenService = verifyEmailTokenService;
        this.forgetPasswordTokenService = forgetPasswordTokenService;
        this.mailService = mailService;
        this.imageServiceFactory = imageServiceFactory;
    }

    public User findUserByUsernameAndActiveTrue(String username) {
        return repository.findUserByUsernameAndActiveTrue(username);
    }

    public int countUsersByRoleAndActiveTrue(Role role) {
        return repository.countUsersByRoleAndActiveTrue(role);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        User userByUsername = repository.findUserByUsernameAndActiveTrue(authenticationRequest.getUsername());

        if (Objects.isNull(userByUsername)) {
            throw new InvalidCredentialsException();
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
        ));

        String token = jwtService.createToken(userByUsername, authenticationRequest.getRememberMe());

        return AuthenticationResponse.builder()
                .token(token)
                .build();

    }

    @Transactional(rollbackOn = Exception.class)
    public AuthenticationResponse authenticateFromGoogle(User user) {

        User userByUsername = repository.findUserByUsernameAndActiveTrue(user.getUsername());

        if (Objects.isNull(userByUsername)) {
            Image savedImage = imageUrlService.save((Object) user.getImage());
            user.setImage(savedImage);
            user.setPassword(passwordEncoder.encode("SuperSecretPassword1!"));
            userByUsername = save(user);
        }

        String token = jwtService.createToken(userByUsername, false);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.isNull(principal)) {
            throw new NoLoggedUserException();
        }
        return findUserByUsernameAndActiveTrue(principal.toString());
    }

    @Override
    public User changeStates(User source, User destination) {
        destination.setName(source.getName());
        destination.setLastName(source.getLastName());
        destination.setUsername(source.getUsername());
        destination.setActive(source.isActive());
        destination.setRole(source.getRole());
        return destination;
    }

    @Override
    public void beforeSaveValidations(User entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setActive(true);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) ||
                !authentication.isAuthenticated() ||
                authentication.getAuthorities().stream().noneMatch(role -> role.getAuthority().equals(Role.ADMIN.getName()))) {

            entity.setRole(Role.USER);
        }
        saveImage(false, "https://objetivoligar.com/wp-content/uploads/2017/03/blank-profile-picture-973460_1280.jpg", entity);
        entity.setFirstTimeAccess(true);
    }

    public ProfileUserDto getProfileInformation() {
        User user = getLoggedUser();
        return mapper.toProfileUserDto(user);
    }

    @Transactional(rollbackOn = Exception.class)
    public User updateUser(UpdateUser updateUser, MultipartFile file, String url) {

        User user = getLoggedUser();

        updateImage(updateUser, file, url, user);

        return user;
    }

    @Transactional
    public User updateUser(UUID id, UpdateUser updateUser, MultipartFile file, String url) {

        User user = findById(id);

        updateImage(updateUser, file, url, user);

        user.setRole(updateUser.getRole());
        user.setActive(updateUser.isActive());

        return user;
    }

    @Transactional
    public AuthenticationResponse updatePassword(NewPassword newPassword) {
        User user = getLoggedUser();
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        user.setFirstTimeAccess(false);

        String token = jwtService.createToken(user, false);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    public void validateEmail(UUID userId, UUID tokenId) {
        User userById = findById(userId);
        VerifyEmailToken tokenById = verifyEmailTokenService.findById(tokenId);

        if (Objects.nonNull(tokenById) && Objects.nonNull(userById)) {
            if (tokenById.getUser().equals(userById) && tokenById.getExpiration().isAfter(LocalDateTime.now())) {
                userById.setEmailConfirmed(true);
                tokenById.setExpiration(LocalDateTime.now());
                return;
            }
        }
        throw new RuntimeException("Token inválido");
    }

    public void forgetPassword(ForgetPasswordRequest request) {

        User user = findUserByUsernameAndActiveTrue(request.getUsername());
        if (Objects.isNull(user)) {
            throw new RuntimeException("Usuari no encontrado");
        }

        ForgetPasswordToken token = new ForgetPasswordToken(user, LocalDateTime.now().plusDays(1));
        ForgetPasswordToken savedToken = forgetPasswordTokenService.save(token);

        mailService.sendHtml("lacodigoneta@gmail.com", user.getUsername(), "¡Bienvenido!",
                "<p>Haga click en el siguiente enlace para restablecer su contraseña</p>" +
                        "<a href='" + ORIGIN_APP + "/resetPassword/" + user.getId() + "/" + savedToken.getId() + "'>"
                        + ORIGIN_APP + "/resetPassword/" + user.getId() + "/" + savedToken.getId() + "</a>"
        );

    }

    public void validateForgetPasswordToken(UUID userId, UUID tokenId) {
        User userById = findById(userId);
        ForgetPasswordToken tokenById = forgetPasswordTokenService.findById(tokenId);

        if (Objects.nonNull(tokenById) && Objects.nonNull(userById)) {
            if (tokenById.getUser().equals(userById) && tokenById.getExpiration().isAfter(LocalDateTime.now())) {
                return;
            }
        }
        throw new RuntimeException("Token inválido");
    }

    @Transactional
    public String resetPassword(UUID userId, UUID tokenId, NewPassword request) {
        validateForgetPasswordToken(userId, tokenId);
        User userById = findById(userId);
        ForgetPasswordToken tokenById = forgetPasswordTokenService.findById(tokenId);

        userById.setPassword(passwordEncoder.encode(request.getNewPassword()));
        tokenById.setExpiration(LocalDateTime.now());
        return "Contraseña cambiada con éxito";
    }

    private void updateImage(UpdateUser updateUser, MultipartFile file, String url, User user) {
        boolean hasFile = Objects.nonNull(file);
        boolean hasUrl = Objects.nonNull(url);

        if (hasFile || (hasUrl && !user.getImage().getLocation().equals(url))) {
            saveImage(hasFile, hasFile ? file : url, user);
        } else if (!hasUrl) {
            saveImage(false, "https://objetivoligar.com/wp-content/uploads/2017/03/blank-profile-picture-973460_1280.jpg", user);
        }

        user.setName(updateUser.getName());
        user.setLastName(updateUser.getLastName());
    }

    private void saveImage(boolean hasFile, Object image, User byId) {
        ImageService imageService = imageServiceFactory.getObject(hasFile);
        Image saved = imageService.save(image);
        byId.setImage(saved);
    }
}
