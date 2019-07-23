package com.bank.core.transaction;

import com.bank.core.transaction.helper.AccountHelper;
import com.bank.core.transaction.helper.Util;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransactionDebit {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionDebit.class);

    public static String debit (BalanceStatus balanceStatus, double amount, int userAccount, int thirdPartyAccount, String prevTransID, List<Transaction> transactionList) {
        balanceStatus.getLock().lock();
        balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() - amount);
        balanceStatus.getLock().unlock();
        String currentTransactionID = AccountHelper.getRandomID();
        Transaction t = new Transaction(userAccount, TransactionType.DEBIT.toString(), amount,
                thirdPartyAccount, Util.getCurrentDateTime(), currentTransactionID, prevTransID);
        LOGGER.info("DEBIT => " + t.toString());
        transactionList.add(t);
        return prevTransID;
    }
}
