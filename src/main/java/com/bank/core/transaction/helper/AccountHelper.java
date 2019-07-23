package com.bank.core.transaction.helper;

import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class AccountHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountHelper.class);

    public static BalanceStatus getBalanceStatus(List<BalanceStatus> balanceStatusList, int accountNumber) {
        return balanceStatusList.stream().filter(x -> x.getAccountNumber().equals(accountNumber)).findFirst().orElse(null);
    }

    public static String getRandomID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Transaction getCurrentTransRecord (List<Transaction> transactionList, String transactionID) {
        LOGGER.info("Fetch transaction record for transactionID " + transactionID);
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
