package com.bank.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    @JsonProperty("accountNumber")
    @NotNull
    private Integer accountNumber;

    public Account() {
    }

    @JsonProperty("accountNumber")
    public Integer getAccountNumber() {
        return accountNumber;
    }
    @JsonProperty("accountNumber")
    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }
}
