package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.NOT_LOGGED_USER;

public class NoLoggedUserException extends RuntimeException{

    public NoLoggedUserException() {
        super(NOT_LOGGED_USER);
    }
}
