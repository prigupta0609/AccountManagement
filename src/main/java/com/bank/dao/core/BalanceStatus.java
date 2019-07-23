package com.bank.dao.core;

import java.util.concurrent.locks.ReentrantLock;

public class BalanceStatus {

    private Integer accountNumber;
    private Boolean onHold;
    private double holdAmount;
    private double availableAmount;
    private String currency;

    private ReentrantLock lock = new ReentrantLock();

    public BalanceStatus() {

    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isOnHold() {
        return onHold;
    }

    public void setOnHold(Boolean onHold) {
        this.onHold = onHold;
    }

    public double getHoldAmount() {
        return holdAmount;
    }

    public void setHoldAmount(double holdAmount) {
        this.holdAmount = holdAmount;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ReentrantLock getLock () {
        return lock;
    }
}
