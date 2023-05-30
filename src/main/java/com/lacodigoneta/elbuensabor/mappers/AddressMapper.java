package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.dto.address.SimpleAddressDto;
import com.lacodigoneta.elbuensabor.entities.Address;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressMapper {

    private ModelMapper mapper;

    public Address toEntity(AddressDto addressDto) {
        return mapper.map(addressDto, Address.class);
    }

    public Address toEntity(SimpleAddressDto simpleAddressDto) {
        return mapper.map(simpleAddressDto, Address.class);
    }

    public AddressDto toAddressDto(Address address) {
        return mapper.map(address, AddressDto.class);
    }

    public SimpleAddressDto toSimpleAddressDto(Address address){
        return mapper.map(address, SimpleAddressDto.class);
    }
}
