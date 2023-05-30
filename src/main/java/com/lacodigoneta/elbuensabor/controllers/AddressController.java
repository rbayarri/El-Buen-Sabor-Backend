package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.address.AddressDto;
import com.lacodigoneta.elbuensabor.entities.Address;
import com.lacodigoneta.elbuensabor.mappers.AddressMapper;
import com.lacodigoneta.elbuensabor.services.AddressService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@AllArgsConstructor
public class AddressController {

    private final AddressService service;

    private final AddressMapper mapper;

    @GetMapping("")
    public ResponseEntity<List<AddressDto>> findAllByUser() {
        return ResponseEntity.ok(service.findAllByUser().stream().map(mapper::toAddressDto).toList());
    }

    @GetMapping("/actives")
    public ResponseEntity<List<AddressDto>> findAllActiveByUser() {
        return ResponseEntity.ok(service.findAllActiveByUser().stream().map(mapper::toAddressDto).toList());
    }

    @PostMapping("")
    public ResponseEntity<AddressDto> save(@RequestBody @Valid AddressDto addressDto) {
        Address address = mapper.toEntity(addressDto);
        Address saved = service.save(address);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequestUri()
                                .path("/{id}")
                                .buildAndExpand(saved.getId())
                                .toUri())
                .body(mapper.toAddressDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> update(@PathVariable UUID id, @RequestBody @Valid AddressDto addressDto) {
        Address address = mapper.toEntity(addressDto);
        Address update = service.update(id, address);
        return ResponseEntity.ok(mapper.toAddressDto(update));
    }

}
