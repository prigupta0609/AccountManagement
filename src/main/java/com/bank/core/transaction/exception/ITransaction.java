package com.bank.core.transaction.exception;

public interface ITransaction {
    public boolean throwTransactionFailed() throws Exception;
}
