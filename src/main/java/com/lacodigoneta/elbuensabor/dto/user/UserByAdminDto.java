package com.lacodigoneta.elbuensabor.dto.user;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
import com.lacodigoneta.elbuensabor.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserByAdminDto {

    private UUID id;

    private String username;

    private Role role;

    private String name;

    private String lastName;

    private Set<AddressDto> addresses;

    private Set<PhoneNumberDto> phoneNumbers;

    private ImageDto image;

    private boolean emailConfirmed;

    private boolean active;

    private boolean firstTimeAccess;
}
