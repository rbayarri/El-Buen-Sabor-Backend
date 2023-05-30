package com.lacodigoneta.elbuensabor.dto.user;

import com.lacodigoneta.elbuensabor.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatedUser {

    private UUID id;

    private String username;

    private Role role;

    private String name;

    private String lastName;

}
