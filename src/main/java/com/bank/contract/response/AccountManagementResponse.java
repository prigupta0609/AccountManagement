package com.bank.contract.response;

import com.bank.contract.Amount;
import com.bank.contract.Payee;
import com.bank.contract.Receiver;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountManagementResponse {

    @JsonProperty("error")
    private Error error;

    @JsonProperty("payee")
    @NotNull
    private Payee payee;

    @JsonProperty("receiver")
    @NotNull
    private Receiver receiver;

    @JsonProperty("amount")
    @NotNull
    private Amount amount;

    public AccountManagementResponse() {}

    @JsonProperty("error")
    public Error getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Error error) {
        this.error = error;
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
