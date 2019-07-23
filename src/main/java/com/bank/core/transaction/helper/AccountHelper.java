package com.bank.core.transaction.helper;

import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountHelper {

    public static BalanceStatus getBalanceStatus(List<BalanceStatus> balanceStatusList, int accountNumber) {
        return balanceStatusList.stream().filter(x -> x.getAccountNumber().equals(accountNumber)).findFirst().orElse(null);
    }

    public static String getRandomID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Transaction getCurrentTransRecord (CopyOnWriteArrayList<Transaction> transactionList, String transactionID) {
        ListIterator<Transaction> iterator = transactionList.listIterator();
        while (iterator.hasNext()) {
            Transaction transaction = iterator.next();
            if (transaction.getCurrentTransactionID().equals(transactionID)) {
                return transaction;
            }
        }
        return null;
    }
}
