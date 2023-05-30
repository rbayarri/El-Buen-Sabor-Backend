package com.lacodigoneta.elbuensabor.dto.user;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
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
public class ProfileUserDto {

    private UUID id;

    private String username;

    private String name;

    private String lastName;

    private Set<AddressDto> addresses;

    private Set<PhoneNumberDto> phoneNumbers;

    private ImageDto image;

    private boolean emailConfirmed;

    private boolean active;
}
