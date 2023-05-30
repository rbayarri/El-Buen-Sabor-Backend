package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INVALID_TOKEN;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException() {
        super(INVALID_TOKEN);
    }
}
