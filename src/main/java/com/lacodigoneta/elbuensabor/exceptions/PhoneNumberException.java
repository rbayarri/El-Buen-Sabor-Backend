package com.lacodigoneta.elbuensabor.exceptions;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INACTIVE_PHONE_NUMBER;

public class PhoneNumberException extends RuntimeException{

    public PhoneNumberException() {
        super(INACTIVE_PHONE_NUMBER);
    }
}
