package com.lacodigoneta.elbuensabor.dto.phonenumber;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePhoneNumberDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private UUID id;

    private String areaCode;

    private String phoneNumber;
}
