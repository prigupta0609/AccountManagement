package com.bank.core.transaction;

import com.bank.core.transaction.helper.AccountHelper;
import com.bank.core.transaction.helper.Util;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransactionCredit {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionCredit.class);

    public static String credit (BalanceStatus balanceStatus, double amount, int userAccount, int thirdPartyAccount, String prevTransID, List<Transaction> transactionList) {
        balanceStatus.getLock().lock();
        balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() + amount);
        balanceStatus.getLock().unlock();
        String currentTransactionID = AccountHelper.getRandomID();
        Transaction t = new Transaction(userAccount, TransactionType.CREDIT.toString(), amount,
                thirdPartyAccount, Util.getCurrentDateTime(), currentTransactionID, prevTransID);
        LOGGER.info("CREDIT => " + t.toString());
        transactionList.add(t);
        return currentTransactionID;
    }
}
