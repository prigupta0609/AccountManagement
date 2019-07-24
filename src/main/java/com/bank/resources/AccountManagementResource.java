package com.bank.resources;

import com.bank.contract.request.AccountManagementRequest;
import com.bank.contract.response.AccountManagementResponse;
import com.bank.core.Error;
import com.bank.core.helper.ResponseBuilder;
import com.bank.core.transaction.TransactionManager;
import com.bank.core.transaction.TransactionStatus;
import com.bank.core.transaction.exception.TransactionFailure;
import com.bank.core.validation.AccountValidation;
import com.bank.core.validation.RequestValidator;
import com.bank.core.validation.ValidationException;
import com.bank.dao.DAO;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource class with handles transfer request directly from client.
 */
@Path("/account")
@Resource
public class AccountManagementResource {

    public static DAO dao;
    public static final RequestValidator requestValidator = new RequestValidator();

    public AccountManagementResource(DAO dao) {
        this.dao = dao;
    }

    /**
     * 1. Validate request
     * 2. Validate accounts
     * 3. Send transfer request
     * @param request
     * @return
     */
    @POST
    @Path("/transfer")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public AccountManagementResponse transferMoney(AccountManagementRequest request) {
        if (requestValidator.isValid(request)) {
            try {
                if (AccountValidation.getInstance().isValidAccounts(request.getPayee(), request.getReceiver(), dao)) {
                    try {
                        TransactionStatus transactionStatus = TransactionManager.getInstance().transfer(request.getReceiver(), request.getPayee(), request.getAmount(), dao);
                        return ResponseBuilder.getAccountManagementResponse(request, this.dao.getBalanceStatus(), transactionStatus, null);
                    } catch (TransactionFailure failure) {
                        return ResponseBuilder.getAccountManagementResponse(request, this.dao.getBalanceStatus(), TransactionStatus.FAIL, failure);
                    }
                }
            } catch (ValidationException exception) {
                return ResponseBuilder.getAccountManagementResponse(request, this.dao.getBalanceStatus(), TransactionStatus.FAIL, exception);
            }
        }
        TransactionFailure invalidRequest = new TransactionFailure(Error.INVALID_REQUEST);
        return ResponseBuilder.getAccountManagementResponse(request, this.dao.getBalanceStatus(), TransactionStatus.FAIL, invalidRequest);
    }
}
