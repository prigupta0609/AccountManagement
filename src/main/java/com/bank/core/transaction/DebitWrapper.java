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

import java.util.List;

public class DebitWrapper implements ITransactionWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebitWrapper.class);
    private Account payeeAccount;
    private Amount amount;
    private Account receiverAccount;
    private TransactionStatus status = TransactionStatus.FAIL;
    private static List<Transaction> transactionList;
    private BalanceStatus balanceStatus = null;
    private static DAO dao;

    public DebitWrapper(DebitWrapperBuilder builder) {
        this.payeeAccount = builder.payeeAccount;
        this.amount = builder.amount;
        this.receiverAccount = builder.receiverAccount;
        this.dao = builder.dao;
        this.balanceStatus = AccountHelper.getBalanceStatus(this.dao.getBalanceStatus(), payeeAccount.getNumber());
        this.transactionList = this.dao.getTransaction();
    }

    public BalanceStatus getBalanceStatus() {
        return balanceStatus;
    }

    public static class DebitWrapperBuilder {

        private Account payeeAccount;
        private Amount amount;
        private Account receiverAccount;
        private DAO dao;

        public DebitWrapperBuilder() {
        }

        public DebitWrapperBuilder withAccountNumber (Account accountNumber) {
            this.payeeAccount = accountNumber;
            return this;
        }

        public DebitWrapperBuilder withAmount (Amount amount) {
            this.amount = amount;
            return this;
        }

        public DebitWrapperBuilder withRecieverAccount (Account account) {
            this.receiverAccount = account;
            return this;
        }

        public DebitWrapperBuilder withDAO (DAO dao) {
            this.dao = dao;
            return this;
        }

        public DebitWrapper build() {
            return new DebitWrapper(this);
        }
    }

    @Override
    public String prepareTransaction(String requestID) throws TransactionFailure {
        String prevTransID = "-1";
        String currentTransactionID = "-1";
        status = TransactionStatus.FAIL;
        if (balanceStatus != null) {
            balanceStatus.getLock().lock();
            double balance = balanceStatus.getAvailableAmount();
            try {
                if (amount.getValue() <= balance) {
                    LOGGER.info("Account locked for prepare transaction for request " + requestID);
                    balanceStatus.setOnHold(true);
                    balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() - amount.getValue());
                    balanceStatus.setHoldAmount(balanceStatus.getHoldAmount() + amount.getValue());
                    currentTransactionID = AccountHelper.getRandomID();
                    transactionList.add(new Transaction(payeeAccount.getNumber(), TransactionType.HOLD.toString(), amount.getValue(), Constants.BANK_ACCOUNT, Util.getCurrentDateTime(), currentTransactionID, prevTransID));
                    status = TransactionStatus.SUCCESS;
                    LOGGER.info("Account released successfully for prepare transaction for request " + requestID);
                    return currentTransactionID;

                } else {
                    status = TransactionStatus.FAIL;
                    throw new TransactionFailure(Error.INSUFFICIENT_BALANCE_ERROR);
                }
            } finally {
                balanceStatus.getLock().unlock();
            }
        } else {
            status = TransactionStatus.FAIL;
            throw new TransactionFailure(Error.ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public String commit(String prevTransactionID, String requestID) throws TransactionFailure {
        Transaction currentTransRecord = AccountHelper.getCurrentTransRecord(transactionList, prevTransactionID);
        if (currentTransRecord != null && currentTransRecord.getTransactionType().equals(TransactionType.HOLD.toString())) {
                balanceStatus.getLock().lock();
                LOGGER.info("Account locked to proceed with commit for request " + requestID + " bank account " + balanceStatus.getAccountNumber());
                balanceStatus.setHoldAmount(balanceStatus.getHoldAmount() - amount.getValue());
                if (balanceStatus.getHoldAmount() <= 0) {
                    balanceStatus.setOnHold(false);
                }
                balanceStatus.getLock().unlock();
                String currentTransactionID = AccountHelper.getRandomID();
                transactionList.add(new Transaction(payeeAccount.getNumber(), TransactionType.DEBIT.toString(), amount.getValue(),
                        receiverAccount.getNumber(), Util.getCurrentDateTime(), currentTransactionID, prevTransactionID));
                status = TransactionStatus.SUCCESS;
                LOGGER.info("Debit confirmed and lock released for request " + requestID);
                return currentTransactionID;

            } else {
            LOGGER.error("Hold amount before debiting from account for request " + requestID + " and transID " + prevTransactionID);
            status = TransactionStatus.FAIL;
            throw new TransactionFailure(Error.TRANSACTION_NOT_PREPARED);
        }

    }

    @Override
    public String rollback(String prevTransactionID, String requestID) throws TransactionFailure {
        Transaction currentTransRecord = AccountHelper.getCurrentTransRecord(transactionList, prevTransactionID);
        if (currentTransRecord != null) {
            prevTransactionID = currentTransRecord.getPrevTransactionID();
                LOGGER.info("Rollback initiated for request " + requestID + " bank account " + balanceStatus.getAccountNumber());
                while (!prevTransactionID.equals("-1")) {
                    switch (currentTransRecord.getTransactionType().toUpperCase()) {
                        case "DEBIT":
                            TransactionCredit.credit(balanceStatus, currentTransRecord.getAmount(), currentTransRecord.getUserAccount(),
                                    currentTransRecord.getThirdPartyAccount(), currentTransRecord.getPrevTransactionID(), transactionList);
                            break;
                        default:
                            LOGGER.error("Invalid state, move to queue for manual intervention : " + currentTransRecord.getCurrentTransactionID());
                            throw new TransactionFailure(Error.INVALID_TRANSACTION);
                    }
                    currentTransRecord = AccountHelper.getCurrentTransRecord(transactionList, prevTransactionID);
                    prevTransactionID = currentTransRecord.getPrevTransactionID();
                }
                status = TransactionStatus.SUCCESS;
                return prevTransactionID;

        } else {
            LOGGER.error("Rollback failed for request " + requestID + " and transID " + prevTransactionID);
            status = TransactionStatus.FAIL;
            throw new TransactionFailure(Error.ROLLBACK_FAILED);
        }
    }

    @Override
    public TransactionStatus getStatus() {
        return status;
    }

}
