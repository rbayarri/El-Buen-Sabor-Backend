package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationRequest;
import com.lacodigoneta.elbuensabor.dto.auth.AuthenticationResponse;
import com.lacodigoneta.elbuensabor.dto.user.NewPassword;
import com.lacodigoneta.elbuensabor.dto.user.ProfileUserDto;
import com.lacodigoneta.elbuensabor.dto.user.UpdateUser;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.Role;
import com.lacodigoneta.elbuensabor.exceptions.InvalidCredentialsException;
import com.lacodigoneta.elbuensabor.exceptions.NoLoggedUserException;
import com.lacodigoneta.elbuensabor.mappers.ImageMapper;
import com.lacodigoneta.elbuensabor.mappers.UserMapper;
import com.lacodigoneta.elbuensabor.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserService extends BaseServiceImpl<User, UserRepository> {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final ImageUrlService imageUrlService;

    private final UserMapper mapper;

    private final ImageMapper imageMapper;

    public UserService(UserRepository repository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       ImageUrlService imageUrlService,
                       UserMapper mapper,
                       ImageMapper imageMapper) {
        super(repository);
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.imageUrlService = imageUrlService;
        this.mapper = mapper;
        this.imageMapper = imageMapper;
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
            Image savedImage = imageUrlService.save(user.getImage());
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
        return null;
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
    }

    public ProfileUserDto getProfileInformation() {
        User user = getLoggedUser();
        return mapper.toProfileUserDto(user);
    }

    @Transactional
    public User updateUser(UpdateUser updateUser) {

        User user = getLoggedUser();

        user.setName(updateUser.getName());
        user.setImage(imageMapper.toEntity(updateUser.getImage()));
        user.setLastName(updateUser.getLastName());

        if (user.getRole().equals(Role.ADMIN)) {
            user.setRole(updateUser.getRole());
            user.setActive(updateUser.isActive());
        }
        return user;
    }

    @Transactional
    public User updateUser(UUID id, UpdateUser updateUser) {

        User user = findById(id);

        user.setName(updateUser.getName());
        user.setImage(imageMapper.toEntity(updateUser.getImage()));
        user.setLastName(updateUser.getLastName());

        user.setRole(updateUser.getRole());
        user.setActive(updateUser.isActive());

        return user;
    }

    @Transactional
    public String updatePassword(NewPassword newPassword) {
        User user = getLoggedUser();
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        return "Contraseña cambiada con éxito";
    }
}
