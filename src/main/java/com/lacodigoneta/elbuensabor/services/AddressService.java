package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Address;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.exceptions.PermissionsException;
import com.lacodigoneta.elbuensabor.repositories.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService extends BaseServiceImpl<Address, AddressRepository> {

    private final UserService userService;

    public AddressService(AddressRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    public List<Address> findAllByUser() {
        User user = userService.getLoggedUser();
        return repository.findAllByUserUsername(user.getUsername());
    }

    public List<Address> findAllActiveByUser() {
        User user = userService.getLoggedUser();
        return repository.findAllByActiveTrueAndUserUsername(user.getUsername());
    }

    @Override
    public Address changeStates(Address source, Address destination) {
        User loggedUser = userService.getLoggedUser();
        if (!loggedUser.equals(destination.getUser())) {
            throw new PermissionsException();
        }
        destination.setPredetermined(source.isPredetermined());
        destination.setActive(source.isActive());
        destination.setApartment(source.getApartment());
        destination.setFloor(source.getFloor());
        destination.setNumber(source.getNumber());
        destination.setDetails(source.getDetails());
        destination.setStreet(source.getStreet());
        destination.setZipCode(source.getZipCode());
        return destination;
    }

    @Override
    public void beforeSaveValidations(Address entity) {
        User user = userService.getLoggedUser();
        user.getAddresses().add(entity);
        entity.setUser(user);
    }
}
