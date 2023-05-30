package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.PhoneNumber;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.exceptions.PermissionsException;
import com.lacodigoneta.elbuensabor.mappers.PhoneNumberMapper;
import com.lacodigoneta.elbuensabor.repositories.PhoneNumberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneNumberService extends BaseServiceImpl<PhoneNumber, PhoneNumberRepository> {

    private final UserService userService;

    public PhoneNumberService(PhoneNumberRepository repository, PhoneNumberMapper mapper, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    public List<PhoneNumber> findAllByUser() {
        User user = userService.getLoggedUser();
        return repository.findAllByUserUsername(user.getUsername());
    }

    public List<PhoneNumber> findAllActiveByUser() {
        User user = userService.getLoggedUser();
        return repository.findAllByActiveTrueAndUserUsername(user.getUsername());
    }

    @Override
    public PhoneNumber changeStates(PhoneNumber source, PhoneNumber destination) {
        User loggedUser = userService.getLoggedUser();
        if (!loggedUser.equals(destination.getUser())) {
            throw new PermissionsException();
        }
        destination.setAreaCode(source.getAreaCode());
        destination.setPhoneNumber(source.getPhoneNumber());
        destination.setActive(source.isActive());
        destination.setPredetermined(source.isPredetermined());
        return destination;
    }

    @Override
    public void beforeSaveValidations(PhoneNumber entity) {
        User user = userService.getLoggedUser();
        user.getPhoneNumbers().add(entity);
        entity.setUser(user);
    }

}
