package com.lacodigoneta.elbuensabor.dto.product;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_NULL_VALIDATION_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleProductDto {

    @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
    private UUID id;

    private String name;

    private ImageDto image;
}
