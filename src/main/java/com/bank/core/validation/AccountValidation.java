package com.bank.core.validation;

import com.bank.contract.Customer;
import com.bank.contract.Payee;
import com.bank.contract.Receiver;
import com.bank.core.Error;
import com.bank.dao.DAO;

import javax.inject.Singleton;
import java.util.List;

/**
 * Validate user and its account
 */
@Singleton
public class AccountValidation {

    private static DAO database;
    private static AccountValidation INSTANCE = new AccountValidation();

    public static AccountValidation getInstance() {
        return INSTANCE;
    }

    public static boolean isValidAccounts (Payee payee, Receiver receiver, DAO dao) throws ValidationException {
        database = dao;
        if (getPayeeAccount(payee) == null) {
            throw new ValidationException(Error.INVALID_PAYEE_ACCOUNT);
        }
        if (getReceiverAccount(receiver) == null) {
            throw new ValidationException(Error.INVALID_RECEIVER_ACCOUNT);
        }
        return true;
    }

    private static com.bank.dao.core.Account getPayeeAccount (Payee payee) {
        com.bank.dao.core.Account accountInfo = null;
        if (isValidCustomer(payee.getCustomer())) {
            accountInfo = getAccountInfo (payee.getAccount(), payee.getCustomer().getId());
        }
        return accountInfo;
    }

    private static com.bank.dao.core.Account getReceiverAccount (Receiver receiver) {
        com.bank.dao.core.Account accountInfo = null;
        if (isValidCustomer(receiver.getCustomer())) {
            accountInfo = getAccountInfo (receiver.getAccount(), receiver.getCustomer().getId());
        }
        return accountInfo;
    }

    private static boolean isValidCustomer (Customer requestedCustomer) {
        List<com.bank.dao.user.Customer> customerList = database.getCustomer();
        return customerList.stream().filter(x -> x.getName().equalsIgnoreCase(requestedCustomer.getName())
                && x.getId().equals(requestedCustomer.getId())).count() == 1;
    }

    private static com.bank.dao.core.Account getAccountInfo (com.bank.contract.Account requestedAccount, String customerId) {
        List<com.bank.dao.core.Account> accountList = database.getAccount();
        return accountList.stream().filter(x -> x.getCustomerId().equals(customerId)
                && x.getNumber().equals(requestedAccount.getAccountNumber())).findFirst().orElse(null);
    }
}
