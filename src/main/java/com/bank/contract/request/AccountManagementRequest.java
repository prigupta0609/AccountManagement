package com.bank.contract.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bank.contract.Amount;
import com.bank.contract.Payee;
import com.bank.contract.Receiver;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountManagementRequest {

    @JsonProperty("payee")
    @NotNull
    private Payee payee;

    @JsonProperty("receiver")
    @NotNull
    private Receiver receiver;

    @JsonProperty("amount")
    @NotNull
    private Amount amount;

    public AccountManagementRequest() {
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
