package com.bank.dao.core;

public enum TransactionType {
    CREDIT("credit"),
    DEBIT("debit"),
    HOLD("hold"),
    RELEASE("release"),
    READY("ready");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
