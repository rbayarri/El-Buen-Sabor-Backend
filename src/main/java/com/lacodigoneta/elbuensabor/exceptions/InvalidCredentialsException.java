package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INVALID_CREDENTIALS;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException() {
        super(INVALID_CREDENTIALS);
    }
}
