package com.bank.core;

public enum  Error {

    INSUFFICIENT_BALANCE_ERROR (1001, "Insufficient balance in payee account"),
    ACCOUNT_NOT_FOUND (1002, "Account information not found"),
    TRANSACTION_NOT_PREPARED (1003, "Hold is not performed before commit in payee account"),
    ROLLBACK_FAILED (1004, "Rollback failed, manual intervention required"),
    INVALID_TRANSACTION (1005, "Invalid transaction state"),
    DEBIT_FAILED (1006, "Debit failed due to unexpected exception"),
    CREDIT_FAILED (1007, "Debit failed due to unexpected exception"),
    INVALID_REQUEST (1008, "Invalid request"),
    DEFAULT_ERROR (1009, "Unexpected error, unable to serve request"),
    INVALID_RECEIVER_ACCOUNT (1010, "Invalid receiver account"),
    INVALID_PAYEE_ACCOUNT (1011, "Invalid payee account");

    private String errorMessage;
    private int errorCode;

    Error (int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode () {
        return this.errorCode;
    }

    public String getErrorMessage () {
        return this.errorMessage;
    }
}
