package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.phonenumber.PhoneNumberDto;
import com.lacodigoneta.elbuensabor.entities.PhoneNumber;
import com.lacodigoneta.elbuensabor.mappers.PhoneNumberMapper;
import com.lacodigoneta.elbuensabor.services.PhoneNumberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/phoneNumbers")
@AllArgsConstructor
public class PhoneNumberController {

    private final PhoneNumberService service;

    private final PhoneNumberMapper mapper;

    @GetMapping("")
    public ResponseEntity<List<PhoneNumberDto>> findAllByUser() {
        return ResponseEntity.ok(service.findAllByUser().stream().map(mapper::toPhoneNumberDto).toList());
    }

    @GetMapping("/actives")
    public ResponseEntity<List<PhoneNumberDto>> findAllActiveByUser() {
        return ResponseEntity.ok(service.findAllActiveByUser().stream().map(mapper::toPhoneNumberDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhoneNumberDto> findByIdByUser(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toPhoneNumberDto(service.findById(id)));
    }


    @PostMapping("")
    public ResponseEntity<PhoneNumberDto> save(@RequestBody @Valid PhoneNumberDto phoneNumberDto) {

        PhoneNumber phoneNumber = mapper.toEntity(phoneNumberDto);
        PhoneNumber saved = service.save(phoneNumber);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequestUri()
                                .build()
                                .toUri())
                .body(mapper.toPhoneNumberDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneNumberDto> update(@PathVariable UUID id, @RequestBody @Valid PhoneNumberDto phoneNumberDto) {
        PhoneNumber phoneNumber = mapper.toEntity(phoneNumberDto);
        PhoneNumber update = service.update(id, phoneNumber);
        return ResponseEntity.ok(mapper.toPhoneNumberDto(update));
    }

}
