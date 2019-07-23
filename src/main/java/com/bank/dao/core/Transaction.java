package com.bank.dao.core;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Transaction {

    private Integer userAccount;
    private String transactionType;
    private Double amount;
    private Integer thirdPartyAccount;
    private DateTime dateTime;
    private String currentTransactionID;
    private String prevTransactionID;

    public Transaction () {
    }

    public Transaction (int userAccount, String transactionType, double amount, int thirdPartyAccount, DateTime dateTime,
                        String currentTransactionID, String prevTransactionID) {
        this.userAccount = userAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.thirdPartyAccount = thirdPartyAccount;
        this.dateTime = dateTime;
        this.currentTransactionID = currentTransactionID;
        this.prevTransactionID = prevTransactionID;
    }

    public Integer getUserAccount() {
        return userAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getThirdPartyAccount() {
        return thirdPartyAccount;
    }

    public void setThirdPartyAccount(Integer thirdPartyAccount) {
        this.thirdPartyAccount = thirdPartyAccount;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        this.dateTime = DateTime.parse(dateTime, formatter);
    }

    public void setUserAccount(Integer userAccount) {
        this.userAccount = userAccount;
    }

    public String getCurrentTransactionID() {
        return currentTransactionID;
    }

    public void setCurrentTransactionID(String currentTransactionID) {
        this.currentTransactionID = currentTransactionID;
    }

    public String getPrevTransactionID() {
        return prevTransactionID;
    }

    public void setPrevTransactionID(String prevTransactionID) {
        this.prevTransactionID = prevTransactionID;
    }

    public String toString() {
        return "UserAccount : " + userAccount + " : transactionType : " + transactionType + " : currentTransID : "
                + currentTransactionID + " : previousTransID : " + prevTransactionID + " : amount : " + amount;

    }
}
