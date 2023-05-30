package com.lacodigoneta.elbuensabor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN"),
    CASHIER("ROLE_CASHIER"),
    CHEF("ROLE_CHEF"),
    DELIVERY("ROLE_DELIVERY"),
    USER("ROLE_USER");

    private final String name;

}
