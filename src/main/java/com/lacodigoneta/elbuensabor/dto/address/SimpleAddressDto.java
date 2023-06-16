package com.lacodigoneta.elbuensabor.dto.address;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleAddressDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private UUID id;

    private String street;

    private String number;

    private String floor;

    private String apartment;

    private String zipCode;

    private String details;
}
