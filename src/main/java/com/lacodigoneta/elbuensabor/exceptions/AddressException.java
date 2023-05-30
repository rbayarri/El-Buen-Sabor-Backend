package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INACTIVE_ADDRESS;

public class AddressException extends RuntimeException{

    public AddressException() {
        super(INACTIVE_ADDRESS);
    }
}
