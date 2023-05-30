package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.FORBIDDEN;

public class PermissionsException extends RuntimeException{

    public PermissionsException() {
        super(FORBIDDEN);
    }
}
