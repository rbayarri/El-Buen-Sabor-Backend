package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.dto.auth.RegistrationRequest;
import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
import com.lacodigoneta.elbuensabor.dto.user.*;
import com.lacodigoneta.elbuensabor.entities.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserMapper {

    private ModelMapper mapper;

    private AddressMapper addressMapper;

    private PhoneNumberMapper phoneNumberMapper;

    private ImageMapper imageMapper;

    public UserDto toUserDto(User user) {
        UserDto userDto = mapper.map(user, UserDto.class);
        if (Objects.nonNull(user.getImage())) {
            userDto.setImage(imageMapper.toImageDto(user.getImage()));
        }
        return userDto;
    }

    public User toEntity(RegistrationRequest registrationRequest) {
        return mapper.map(registrationRequest, User.class);
    }

    public User toEntity(UpdateUser updateUser) {
        return mapper.map(updateUser, User.class);
    }

    public CreatedUser toCreatedUser(User user) {
        return mapper.map(user, CreatedUser.class);
    }

    public ProfileUserDto toProfileUserDto(User user) {
        ProfileUserDto profileUserDto = mapper.map(user, ProfileUserDto.class);

        if (Objects.nonNull(user.getImage())) {
            profileUserDto.setImage(imageMapper.toImageDto(user.getImage()));
        }

        if (Objects.nonNull(user.getAddresses())) {
            Set<AddressDto> addressDtoSet = user.getAddresses().stream().map(addressMapper::toAddressDto).collect(Collectors.toSet());
            profileUserDto.setAddresses(addressDtoSet);
        }
        if (Objects.nonNull(user.getPhoneNumbers())) {
            Set<PhoneNumberDto> phoneNumberDtoSet = user.getPhoneNumbers().stream().map(phoneNumberMapper::toPhoneNumberDto).collect(Collectors.toSet());
            profileUserDto.setPhoneNumbers(phoneNumberDtoSet);
        }
        return profileUserDto;
    }

    public UserByAdminDto toUserByAdminDto(User user) {

        UserByAdminDto userByAdminDto = mapper.map(user, UserByAdminDto.class);

        if (Objects.nonNull(user.getImage())) {
            userByAdminDto.setImage(imageMapper.toImageDto(user.getImage()));
        }

        if (Objects.nonNull(user.getAddresses())) {
            Set<AddressDto> addressDtoSet = user.getAddresses().stream().map(addressMapper::toAddressDto).collect(Collectors.toSet());
            userByAdminDto.setAddresses(addressDtoSet);
        }
        if (Objects.nonNull(user.getPhoneNumbers())) {
            Set<PhoneNumberDto> phoneNumberDtoSet = user.getPhoneNumbers().stream().map(phoneNumberMapper::toPhoneNumberDto).collect(Collectors.toSet());
            userByAdminDto.setPhoneNumbers(phoneNumberDtoSet);
        }

        return userByAdminDto;

    }
}
