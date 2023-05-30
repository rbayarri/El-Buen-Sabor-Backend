package com.lacodigoneta.elbuensabor.dto.address;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_EMPTY_VALIDATION_MESSAGE;
import static com.lacodigoneta.elbuensabor.config.AppConstants.NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {

    @Null(message = NULL_VALIDATION_MESSAGE)
    private UUID id;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String street;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String number;

    private String floor;

    private String apartment;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String zipCode;

    private String details;

    private boolean isPredetermined;

    private boolean active;

}
