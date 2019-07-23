package com.bank.core.transaction;

import com.bank.core.transaction.helper.AccountHelper;

public class CompositeTransactionLogic {

    private ITransactionWrapper receiver;
    private ITransactionWrapper payee;
    private Coordinator coordinator = null;
    private String requestID;

    CompositeTransactionLogic() {
        this.coordinator = new Coordinator();
    }

    public void register (ITransactionWrapper payee, ITransactionWrapper receiver) {
        this.requestID = AccountHelper.getRandomID();
        this.receiver = receiver;
        this.payee = payee;
    }

    public TransactionStatus startTransaction() {
        return coordinator.startTransaction(receiver, payee, requestID);
    }

    public TransactionStatus commit() {
        return coordinator.commit(receiver, payee, requestID);
    }
}
