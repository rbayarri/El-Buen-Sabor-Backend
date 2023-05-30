package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
import com.lacodigoneta.elbuensabor.dto.phonenumber.SimplePhoneNumberDto;
import com.lacodigoneta.elbuensabor.entities.PhoneNumber;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PhoneNumberMapper {

    private final ModelMapper mapper;

    public PhoneNumber toEntity(PhoneNumberDto phoneNumberDto) {
        return mapper.map(phoneNumberDto, PhoneNumber.class);
    }

    public PhoneNumber toEntity(SimplePhoneNumberDto simplePhoneNumberDto) {
        return mapper.map(simplePhoneNumberDto, PhoneNumber.class);
    }

    public PhoneNumberDto toPhoneNumberDto(PhoneNumber phoneNumber) {
        return mapper.map(phoneNumber, PhoneNumberDto.class);
    }

    public SimplePhoneNumberDto toSimplePhoneNumberDto(PhoneNumber phoneNumber) {
        return mapper.map(phoneNumber, SimplePhoneNumberDto.class);
    }

}
