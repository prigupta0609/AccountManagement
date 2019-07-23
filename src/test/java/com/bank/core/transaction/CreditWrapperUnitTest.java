package com.bank.core.transaction;

import com.bank.contract.Amount;
import com.bank.core.transaction.exception.TransactionFailure;
import com.bank.dao.core.Account;
import com.bank.dao.core.Transaction;
import com.bank.dao.core.TransactionType;
import com.bank.helper.Utility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

public class CreditWrapperUnitTest {

    private static final double CREDIT_AMOUNT = 10.0;
    public CreditWrapper creditWrapper;
    public CreditWrapper.CreditWrapperBuilder creditWrapperBuilder;
    private static final String DEFAULT_REQUEST_ID = "1";

    @Before
    public void setUp () {
        this.creditWrapperBuilder = buildCreditWrapperBuilder();
        this.creditWrapper = buildDefaultCreditWrapper(this.creditWrapperBuilder);
    }

    @Test
    public void testPrepareTransaction () {
        String  transID = creditWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.creditWrapper.getStatus());
        validateTransaction(transID, TransactionType.READY, CREDIT_AMOUNT);
    }

    @Test
    public void testCommit_Success () {
        String preparedID = creditWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        String  transID = creditWrapper.commit(preparedID, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.creditWrapper.getStatus());
        validateTransaction(transID, TransactionType.CREDIT, CREDIT_AMOUNT);
    }

    @Test(expected = TransactionFailure.class)
    public void testCommit_Fail () {
        Account userAccount = new Account();
        userAccount.setNumber(11121);
        userAccount.setCustomerId("cust1");
        creditWrapperBuilder.withReceiverAccount(userAccount).build();
        creditWrapper = buildDefaultCreditWrapper(creditWrapperBuilder);
        creditWrapper.commit("12", DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, this.creditWrapper.getStatus());
    }

    @Test
    public void testRollback_Success () {
        String preparedID = creditWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        String transID = creditWrapper.commit(preparedID, DEFAULT_REQUEST_ID);
        String result = creditWrapper.rollback(transID, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.creditWrapper.getStatus());
        Assert.assertEquals(result, "-1");
    }

    @Test(expected=TransactionFailure.class)
    public void testRollback_Fail() {
        String holdTransID = creditWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        creditWrapper.commit(holdTransID, DEFAULT_REQUEST_ID);
        creditWrapper.rollback("-1", DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, this.creditWrapper.getStatus());
    }

    private void validateTransaction(String transactionID, TransactionType transactionType, Double amount) {
        try {
            Field transactionList = CreditWrapper.class.getDeclaredField("transactionList");
            transactionList.setAccessible(true);
            List<Transaction> transactions = (List<Transaction>) transactionList.get(null);
            Assert.assertEquals(transactions.stream().filter(x -> x.getCurrentTransactionID() == transactionID).count(), 1);
            Transaction transaction = transactions.stream().filter(x -> x.getCurrentTransactionID() == transactionID).findFirst().orElse(null);
            Assert.assertNotNull(transaction);
            Assert.assertEquals(transaction.getTransactionType(), transactionType.toString());
            Assert.assertEquals(transaction.getAmount(), amount);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private CreditWrapper buildDefaultCreditWrapper (CreditWrapper.CreditWrapperBuilder creditWrapperBuilder) {
        return creditWrapperBuilder.build();
    }

    private CreditWrapper.CreditWrapperBuilder buildCreditWrapperBuilder() {
        Account thirdPartyAccount = new Account();
        thirdPartyAccount.setNumber(1111);
        thirdPartyAccount.setCustomerId("cust1");
        Amount amountReceived = new Amount();
        amountReceived.setValue(CREDIT_AMOUNT);
        amountReceived.setCurrency("EUR");
        Account userAccount = new Account();
        userAccount.setNumber(2222);
        userAccount.setCustomerId("cust2");

        return new CreditWrapper.CreditWrapperBuilder()
                .withAccountNumber(thirdPartyAccount)
                .withAmount(amountReceived)
                .withReceiverAccount(userAccount)
                .withDAO(Utility.getDummyData());
    }
}
