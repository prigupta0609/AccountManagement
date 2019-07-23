package com.bank.core.transaction;

import com.bank.core.transaction.exception.TransactionFailure;

public interface ITransactionWrapper {

    public String prepareTransaction(String requestID) throws TransactionFailure;
    public String commit(String prevTransactionID, String requestID) throws TransactionFailure;
    public String rollback(String prevTransactionID, String requestID) throws TransactionFailure;
    public TransactionStatus getStatus();
}
