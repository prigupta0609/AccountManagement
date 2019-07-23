package com.bank.core.transaction;

import com.bank.core.transaction.helper.AccountHelper;
import com.bank.core.transaction.helper.Util;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;

import java.util.List;

public class TransactionDebit {

    public static String debit (BalanceStatus balanceStatus, double amount, int userAccount, int thirdPartyAccount, String prevTransID, List<Transaction> transactionList) {
        balanceStatus.getLock().lock();
        balanceStatus.setAvailableAmount(balanceStatus.getAvailableAmount() - amount);
        balanceStatus.getLock().unlock();
        String currentTransactionID = AccountHelper.getRandomID();
        transactionList.add(new Transaction(userAccount, TransactionType.DEBIT.toString(), amount,
                thirdPartyAccount, Util.getCurrentDateTime(), currentTransactionID, prevTransID));
        return prevTransID;
    }
}
