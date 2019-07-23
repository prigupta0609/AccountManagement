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

public class DebitWrapperUnitTest {

    public DebitWrapper debitWrapper;
    public DebitWrapper.DebitWrapperBuilder debitWrapperBuilder;
    private static final double HOLD_AMOUNT = 10.0;
    private static final String DEFAULT_REQUEST_ID = "dads1";

    @Before
    public void setUp () {
        this.debitWrapperBuilder = buildDebitWrapperBuilder();
        this.debitWrapper = buildDefaultDebitWrapper(this.debitWrapperBuilder);
    }

    @Test
    public void testPrepareTransaction_Success() {
        String transactionID = debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.debitWrapper.getStatus());
        Assert.assertEquals(HOLD_AMOUNT, debitWrapper.getBalanceStatus().getHoldAmount(), 0);
        Assert.assertTrue(debitWrapper.getBalanceStatus().isOnHold());
        validateTransaction(transactionID, TransactionType.HOLD, HOLD_AMOUNT);
    }

    @Test(expected = TransactionFailure.class)
    public void testPrepareTransaction_InsufficientBalance() {
        Amount transferrableAmount = new Amount();
        transferrableAmount.setValue(1000.00);
        transferrableAmount.setCurrency("EUR");
        debitWrapperBuilder.withAmount(transferrableAmount).build();
        debitWrapper = buildDefaultDebitWrapper(debitWrapperBuilder);
        debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, this.debitWrapper.getStatus());
        Assert.assertNull(debitWrapper.getBalanceStatus());
    }

    @Test(expected = TransactionFailure.class)
    public void testPrepareTransaction_AccountMissing() {
        Account userAccount = new Account();
        userAccount.setNumber(11121);
        userAccount.setCustomerId("cust1");
        debitWrapperBuilder.withAccountNumber(userAccount).build();
        debitWrapper = buildDefaultDebitWrapper(debitWrapperBuilder);
        debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, this.debitWrapper.getStatus());
        Assert.assertNull(debitWrapper.getBalanceStatus());
    }

    @Test(expected = TransactionFailure.class)
    public void testCommit_Fail_PrepareNotDonePrior() {
        debitWrapper.commit("1111", "2");
        Assert.assertEquals(TransactionStatus.FAIL, this.debitWrapper.getStatus());
    }

    @Test
    public void testCommit_Success() {
        String transactionID = debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        debitWrapper.commit(transactionID, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.debitWrapper.getStatus());
    }

    @Test
    public void testRollback_Success() {
        String holdTransID = debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        String commitTransID = debitWrapper.commit(holdTransID, DEFAULT_REQUEST_ID);
        String result = debitWrapper.rollback(commitTransID, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, this.debitWrapper.getStatus());
        Assert.assertEquals(result, "-1");
    }

    @Test(expected = TransactionFailure.class)
    public void testRollback_Fail() {
        String holdTransID = debitWrapper.prepareTransaction(DEFAULT_REQUEST_ID);
        debitWrapper.commit(holdTransID, DEFAULT_REQUEST_ID);
        debitWrapper.rollback("-1", DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, this.debitWrapper.getStatus());
    }

    private DebitWrapper buildDefaultDebitWrapper (DebitWrapper.DebitWrapperBuilder debitWrapperBuilder) {
        return debitWrapperBuilder.build();
    }

    private DebitWrapper.DebitWrapperBuilder buildDebitWrapperBuilder() {
        Account userAccount = new Account();
        userAccount.setNumber(1111);
        userAccount.setCustomerId("cust1");
        Amount transferableAmount = new Amount();
        transferableAmount.setValue(HOLD_AMOUNT);
        transferableAmount.setCurrency("EUR");
        Account thirdPartyAccount = new Account();
        thirdPartyAccount.setNumber(2222);
        thirdPartyAccount.setCustomerId("cust2");

        return new DebitWrapper.DebitWrapperBuilder()
                .withAccountNumber(userAccount)
                .withAmount(transferableAmount)
                .withReceiverAccount(thirdPartyAccount)
                .withDAO(Utility.getDummyData());
    }

    private void validateTransaction(String transactionID, TransactionType transactionType, Double amount) {
        try {
            Field transactionList = DebitWrapper.class.getDeclaredField("transactionList");
            transactionList.setAccessible(true);
            List<Transaction> transactions = (List<Transaction>) transactionList.get(null);
            Assert.assertEquals(transactions.stream().filter(x -> x.getCurrentTransactionID().equals(transactionID)).count(), 1);
            Transaction transaction = transactions.stream().filter(x -> x.getCurrentTransactionID().equals(transactionID)).findFirst().orElse(null);
            Assert.assertNotNull(transaction);
            Assert.assertEquals(transaction.getTransactionType(), transactionType.toString());
            Assert.assertEquals(transaction.getAmount(), amount);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
