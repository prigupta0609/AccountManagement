package com.bank.dao;

import com.bank.dao.core.Account;
import com.bank.dao.core.BalanceStatus;
import com.bank.dao.core.Transaction;
import com.bank.dao.user.Customer;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class with keeps the database in in-memory
 */
@Singleton
public class DAO{

    private List<Customer> customer;
    private List<Account> account;
    private CopyOnWriteArrayList<Transaction> transaction;
    private List<BalanceStatus> balanceStatus;

    private static DAO INSTANCE = new DAO();

    public static DAO getInstance() {
        return INSTANCE;
    }

    public List<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(List<Customer> customer) {
        this.customer = customer;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }

    public CopyOnWriteArrayList<Transaction> getTransaction() {
        return transaction;
    }

    public void setTransaction(List<Transaction> transaction) {
        this.transaction = new CopyOnWriteArrayList<>(transaction);
    }

    public List<BalanceStatus> getBalanceStatus() {
        return balanceStatus;
    }

    public void setBalanceStatus(List<BalanceStatus> balanceStatus) {
        this.balanceStatus = balanceStatus;
    }
}
