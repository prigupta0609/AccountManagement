package com.bank.core.transaction.exception;

import com.bank.core.Error;

public class TransactionFailure extends RuntimeException {

    public TransactionFailure(Error message) {
        super(message.getErrorMessage());
    }
}
