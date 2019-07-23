package com.bank.contract;

import com.bank.contract.response.IAccountHolder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payee implements IAccountHolder {

    @JsonProperty("customer")
    @NotNull
    private Customer customer;

    @JsonProperty("account")
    @NotNull
    private Account account;

    @JsonProperty("accountBalance")
    @Nullable
    private Amount accountBalance;

    public Payee() {
    }

    @JsonProperty("customer")
    @Override
    public Customer getCustomer() {
        return customer;
    }
    @JsonProperty("customer")
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    @JsonProperty("account")
    @Override
    public Account getAccount() {
        return account;
    }
    @JsonProperty("account")
    public void setAccount(Account account) {
        this.account = account;
    }

    @Nullable
    @Override
    public Amount getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(@Nullable Amount accountBalance) {
        this.accountBalance = accountBalance;
    }
}
