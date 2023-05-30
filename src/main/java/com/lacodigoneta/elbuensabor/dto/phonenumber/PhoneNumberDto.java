package com.lacodigoneta.elbuensabor.dto.phonenumber;

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
public class PhoneNumberDto {

    @Null(message = NULL_VALIDATION_MESSAGE)
    private UUID id;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String areaCode;

    @NotEmpty(message = NOT_EMPTY_VALIDATION_MESSAGE)
    private String phoneNumber;

    private boolean isPredetermined;

    private boolean active;

}
