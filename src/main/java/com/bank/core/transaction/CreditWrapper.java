package com.bank.core.transaction;

import com.bank.contract.Amount;
import com.bank.core.Error;
import com.bank.core.transaction.exception.TransactionFailure;
import com.bank.core.transaction.helper.AccountHelper;
import com.bank.core.transaction.helper.Util;
import com.bank.dao.DAO;
import com.bank.dao.core.Account;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class CreditWrapper implements ITransactionWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditWrapper.class);
    private Account payeeAccount;
    private Amount amount;
    private Account receiverAccount;
    private static CopyOnWriteArrayList<Transaction> transactionList;
    private TransactionStatus status;
    private BalanceStatus balanceStatus = null;
    private static DAO dao;
    private static final String CREDIT = "CREDIT";


    public CreditWrapper(CreditWrapperBuilder builder) {
        this.payeeAccount = builder.payeeAccount;
        this.amount = builder.amount;
        this.receiverAccount = builder.receiverAccount;
        this.dao = builder.dao;
        this.transactionList = this.dao.getTransaction();
        this.balanceStatus = AccountHelper.getBalanceStatus(this.dao.getBalanceStatus(), receiverAccount.getNumber());
    }

    public static class CreditWrapperBuilder {

        private Account payeeAccount;
        private Amount amount;
        private Account receiverAccount;
        private DAO dao;

        public CreditWrapperBuilder() {
        }

        public CreditWrapperBuilder withAccountNumber (Account accountNumber) {
            this.payeeAccount = accountNumber;
            return this;
        }

        public CreditWrapperBuilder withAmount (Amount amount) {
            this.amount = amount;
            return this;
        }

        public CreditWrapperBuilder withReceiverAccount(Account account) {
            this.receiverAccount = account;
            return this;
        }

        public CreditWrapperBuilder withDAO (DAO dao) {
            this.dao = dao;
            return this;
        }

        public CreditWrapper build() {
            return new CreditWrapper(this);
        }
    }

    @Override
    public String prepareTransaction(String requestID) throws TransactionFailure {
        // Do all checks here like whether account is freeze or not and accordingly send back the status.
        // As of now, making it hard SUCCESS
        LOGGER.info("Preparing credit transaction successful for request " + requestID);
        String currentTransactionID = AccountHelper.getRandomID();
        transactionList.add(new Transaction(receiverAccount.getNumber(), TransactionType.READY.toString(), amount.getValue(),
                payeeAccount.getNumber(), Util.getCurrentDateTime(), currentTransactionID, Constants.DEFAULT_TRANS_ID));
        status = TransactionStatus.SUCCESS;
        return currentTransactionID;
    }

    @Override
    public String commit(String prevTransactionID, String requestID) throws TransactionFailure {
        String currentTransactionID = AccountHelper.getRandomID();

        if (balanceStatus != null) {
            balanceStatus.getLock().lock();
            LOGGER.info("Account locked for committing transaction for request " + requestID + " bank account " + balanceStatus.getAccountNumber());
            balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() + amount.getValue());
            balanceStatus.getLock().unlock();
            transactionList.add(new Transaction(receiverAccount.getNumber(), TransactionType.CREDIT.toString(), amount.getValue(),
                    payeeAccount.getNumber(), Util.getCurrentDateTime(), currentTransactionID, prevTransactionID));
            status = TransactionStatus.SUCCESS;
            LOGGER.info("Committing transaction successful for request " + requestID);
            return currentTransactionID;

        } else {
            status = TransactionStatus.FAIL;
            throw new TransactionFailure(Error.ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public String rollback(String prevTransactionID, String requestID) throws TransactionFailure {
        if (balanceStatus != null) {
                Transaction currentTransRecord = AccountHelper.getCurrentTransRecord(transactionList, prevTransactionID);
                if (currentTransRecord != null) {
                    prevTransactionID = currentTransRecord.getPrevTransactionID();
                    while (!prevTransactionID.equals(Constants.DEFAULT_TRANS_ID)) {
                        switch (currentTransRecord.getTransactionType().toUpperCase()) {
                            case CREDIT:
                                LOGGER.info("Rollback initiated for request " + requestID + " bank account " + balanceStatus.getAccountNumber());
                                TransactionDebit.debit(balanceStatus, currentTransRecord.getAmount(), currentTransRecord.getUserAccount(),
                                        currentTransRecord.getThirdPartyAccount(), currentTransRecord.getPrevTransactionID(),
                                        transactionList);
                                break;
                            default:
                                status = TransactionStatus.FAIL;
                                LOGGER.error("Invalid transaction record found for request " + requestID);
                                throw new TransactionFailure(Error.INVALID_TRANSACTION);
                        }
                        currentTransRecord = AccountHelper.getCurrentTransRecord(transactionList, prevTransactionID);
                        prevTransactionID = currentTransRecord.getPrevTransactionID();
                    }

                    status = TransactionStatus.SUCCESS;
                    return Constants.DEFAULT_TRANS_ID;
                } else {
                    status = TransactionStatus.FAIL;
                    LOGGER.error("Account information not found for request " + requestID);
                    throw new TransactionFailure(Error.ROLLBACK_FAILED);
                }

        } else {
            status = TransactionStatus.FAIL;
            LOGGER.error("Account information not found for request " + requestID);
            throw new TransactionFailure(Error.ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public TransactionStatus getStatus() {
        return status;
    }
}
