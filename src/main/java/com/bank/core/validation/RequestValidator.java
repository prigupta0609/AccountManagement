package com.bank.core.validation;

import com.bank.contract.request.AccountManagementRequest;

public class RequestValidator implements IValidator<AccountManagementRequest> {

    @Override
    public boolean isValid(AccountManagementRequest request) throws ValidationException {
        // All validations for request goes here. If there is any error, throw ValidationException
        return true;
    }
}
