package com.nathanlucas.nscommerce.services.exceptions;

public class DatabaseException extends RuntimeException {

    public DatabaseException() {
    }

    public DatabaseException(String msg) {
        super(msg);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
