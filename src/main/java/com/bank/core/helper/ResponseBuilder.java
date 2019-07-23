package com.bank.core.helper;

import com.bank.contract.Account;
import com.bank.contract.Amount;
import com.bank.contract.request.AccountManagementRequest;
import com.bank.contract.response.AccountManagementResponse;
import com.bank.contract.response.Error;
import com.bank.core.transaction.TransactionStatus;
import com.bank.dao.core.BalanceStatus;

import java.util.List;

public class ResponseBuilder {

    public static AccountManagementResponse getAccountManagementResponse (AccountManagementRequest request,
                                                                          List<BalanceStatus> balanceStatus,
                                                                          TransactionStatus status,
                                                                          Exception failure) {
        AccountManagementResponse response = new AccountManagementResponse();
        response.setReceiver(request.getReceiver());
        response.getReceiver().setAccountBalance(getBalance(balanceStatus, request.getReceiver().getAccount()));
        response.setPayee(request.getPayee());
        response.getPayee().setAccountBalance(getBalance(balanceStatus, request.getPayee().getAccount()));
        if (status.equals(TransactionStatus.FAIL) && failure != null) {
            Error error = new Error();
            error.setDescription(failure.getMessage());
            response.setError(error);
        }
        return response;
    }

    private static Amount getBalance (List<BalanceStatus> balanceStatus, Account account) {
        BalanceStatus balance = balanceStatus.stream().filter(x -> x.getAccountNumber().equals(account.getAccountNumber()))
                .findFirst().orElse(null);
        if (balance != null) {
            return new Amount(balance.getAvailableAmount(), balance.getCurrency());
        }
        return null;
    }
}
