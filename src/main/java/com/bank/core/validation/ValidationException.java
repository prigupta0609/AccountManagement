package com.bank.core.validation;

import com.bank.core.Error;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException (Error error) {
        super(error.getErrorMessage());
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
