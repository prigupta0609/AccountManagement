package com.bank.core.transaction;

import com.bank.core.transaction.helper.AccountHelper;
import com.bank.core.transaction.helper.Util;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;

import java.util.concurrent.CopyOnWriteArrayList;

public class TransactionCredit {

    public static String credit (BalanceStatus balanceStatus, double amount, int userAccount, int thirdPartyAccount, String prevTransID, CopyOnWriteArrayList<Transaction> transactionList) {
        balanceStatus.getLock().lock();
        balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() + amount);
        balanceStatus.getLock().unlock();
        String currentTransactionID = AccountHelper.getRandomID();
        transactionList.add(new Transaction(userAccount, TransactionType.CREDIT.toString(), amount,
                thirdPartyAccount, Util.getCurrentDateTime(), currentTransactionID, prevTransID));
        return currentTransactionID;
    }
}
