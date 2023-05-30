package com.lacodigoneta.elbuensabor.dto.user;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String username;

    private String name;

    private String lastName;

    private ImageDto image;
}
