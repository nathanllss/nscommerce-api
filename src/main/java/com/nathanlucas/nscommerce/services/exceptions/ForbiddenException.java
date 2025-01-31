package com.nathanlucas.nscommerce.services.exceptions;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
    }

    public ForbiddenException(String msg) {
        super(msg);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
