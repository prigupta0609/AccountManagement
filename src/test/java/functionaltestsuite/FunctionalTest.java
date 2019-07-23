package functionaltestsuite;

import com.bank.contract.request.AccountManagementRequest;
import com.bank.contract.response.AccountManagementResponse;
import com.bank.contract.response.IAccountHolder;
import com.bank.dao.DAO;
import com.bank.dao.core.BalanceStatus;
import com.bank.helper.Utility;
import com.bank.resources.AccountManagementResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FunctionalTest {

    private static AccountManagementResource resource;
    private static final String transferRequest = "src/test/resources/TransferRequest.json";
    private static final String transferResponse = "src/test/resources/TransferResponse.json";
    private static final String transferRequest_InsufficientBalance = "src/test/resources/Failed_TransferRequest_InsufficientBalance.json";
    private static final String transferRequest_InvalidAccount = "src/test/resources/Failed_TransferRequest_InvalidAccount.json";
    private DAO dao;

    @Before
    public void setUp () {
        dao = Utility.getDummyData();
        resource = new AccountManagementResource(dao);
    }

    @Test
    public void testTransferAPI () {
        AccountManagementResponse response = resource.transferMoney(getTransferRequest(transferRequest));
        AccountManagementResponse expectedResponse = getTransferResponse(transferResponse);
        Assert.assertEquals(response.getPayee().getAccountBalance().getValue(), expectedResponse.getPayee().getAccountBalance().getValue());
        Assert.assertEquals(response.getReceiver().getAccountBalance().getValue(), expectedResponse.getReceiver().getAccountBalance().getValue());
    }

    @Test
    public void testMultipleTransferRequest () {
        AccountManagementRequest request = getTransferRequest(transferRequest);
        double payeeAccountBalance = getAccountBalance(request.getPayee()).getAvailableAmount();
        double receiverAccountBalance = getAccountBalance(request.getReceiver()).getAvailableAmount();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<AccountManagementResponse>> futureList = new ArrayList<>(3);
        for (int i=0; i<3; i++) {
            futureList.add(executor.submit(new Callable<AccountManagementResponse>() {
                public AccountManagementResponse call() {
                    return resource.transferMoney(request);
                }}));
        }
        try {
            for (int j=0; j<3 ;j++) {
                futureList.get(j).get(5, TimeUnit.SECONDS);
            }
            BalanceStatus actualPayeeAccountBalance = getAccountBalance(request.getPayee());
            BalanceStatus actualReceiverAccountBalance = getAccountBalance(request.getReceiver());
            Assert.assertEquals((payeeAccountBalance - 3*request.getAmount().getValue()), actualPayeeAccountBalance.getAvailableAmount(), 0);
            Assert.assertEquals((receiverAccountBalance + 3*request.getAmount().getValue()), actualReceiverAccountBalance.getAvailableAmount(), 0);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTransferAPI_Failed_InsufficientBalance () {
        AccountManagementResponse response = resource.transferMoney(getTransferRequest(transferRequest_InsufficientBalance));
        Assert.assertEquals("Insufficient balance in payee account", response.getError().getDescription());
    }

    @Test
    public void testTransferAPI_Failed_InvalidAccount () {
        AccountManagementResponse response = resource.transferMoney(getTransferRequest(transferRequest_InvalidAccount));
        Assert.assertEquals("Invalid payee account", response.getError().getDescription());
    }

    private AccountManagementRequest getTransferRequest (String fileName) {
        AccountManagementRequest request = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            request = objectMapper.readValue(new File(fileName), AccountManagementRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    private AccountManagementResponse getTransferResponse (String fileName) {
        AccountManagementResponse response = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            response = objectMapper.readValue(new File(fileName), AccountManagementResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private BalanceStatus getAccountBalance (IAccountHolder payeeAccount) {
        return dao.getBalanceStatus().stream().filter(x -> x.getAccountNumber().equals(payeeAccount.getAccount().getAccountNumber())).findFirst().get();
    }
}
