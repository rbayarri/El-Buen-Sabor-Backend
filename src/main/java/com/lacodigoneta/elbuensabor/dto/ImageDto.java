package com.lacodigoneta.elbuensabor.dto;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {

    private UUID id;

    @Null(message = NULL_VALIDATION_MESSAGE)
    private String location;
}
