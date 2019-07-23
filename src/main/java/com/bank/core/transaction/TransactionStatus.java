package com.bank.core.transaction;

public enum TransactionStatus {
    SUCCESS ("success"),
    FAIL ("fail");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return this.status;
    }
}
