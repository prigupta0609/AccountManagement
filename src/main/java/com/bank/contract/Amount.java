package com.bank.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

    @JsonProperty("value")
    @NotNull
    private Double value;

    @JsonProperty("currency")
    @NotNull
    private String currency;

    public Amount() {
    }

    public Amount (Double value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    @JsonProperty("value")
    public Double getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Double value) {
        this.value = value;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
