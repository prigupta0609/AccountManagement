package com.bank.contract.response;

import com.bank.contract.Account;
import com.bank.contract.Amount;
import com.bank.contract.Customer;

public interface IAccountHolder {
    public Customer getCustomer();
    public Account getAccount();
    public Amount getAccountBalance();
}
