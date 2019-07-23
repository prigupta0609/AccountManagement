package com.bank.core.transaction;

import com.bank.core.Error;
import com.bank.core.transaction.exception.TransactionFailure;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class CoordinatorUnitTest {

    private ITransactionWrapper debitWrapper;
    private ITransactionWrapper creditWrapper;
    private static Coordinator coordinator;
    private static final String DEFAULT_REQUEST_ID = "1";
    private static final String DEFAULT_TRANS_ID = "abc12";

    @BeforeClass
    public static void setUp () {
        coordinator = new Coordinator();
    }

    @Test
    public void testStartTransaction_Success () {
        setDebitPrepareSuccess();
        setCreditPrepareSuccess();
        TransactionStatus result = this.coordinator.startTransaction(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, result);
    }

    @Test (expected = TransactionFailure.class)
    public void testStartTransaction_CreditFail () {
        setDebitPrepareSuccess();
        setCreditPrepareFailure();
        TransactionStatus result = this.coordinator.startTransaction(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, result);
    }

    @Test (expected = TransactionFailure.class)
    public void testStartTransaction_DebitFail () {
        setDebitPrepareFailure();
        setCreditPrepareSuccess();
        TransactionStatus result = this.coordinator.startTransaction(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, result);
    }

    @Test (expected = TransactionFailure.class)
    public void testStartTransaction_Fail () {
        setDebitPrepareFailure();
        setCreditPrepareFailure();
        TransactionStatus result = this.coordinator.startTransaction(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, result);
    }

    @Test
    public void testCommit_Success () {
        setDebitCommitSuccess();
        setCreditCommitSuccess();
        TransactionStatus result = this.coordinator.commit(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.SUCCESS, result);
    }

    @Test (expected = TransactionFailure.class)
    public void testCommit_DebitFail () {
        setDebitCommitFailure();
        setCreditCommitSuccess();
        TransactionStatus result = this.coordinator.commit(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, result);
    }

    @Test (expected = TransactionFailure.class)
    public void testCommit_CreditFail () {
        setDebitCommitSuccess();
        setCreditCommitFailure();
        TransactionStatus result = this.coordinator.commit(creditWrapper, debitWrapper, DEFAULT_REQUEST_ID);
        Assert.assertEquals(TransactionStatus.FAIL, result);
    }

    private void setDebitPrepareSuccess() {
        this.debitWrapper = Mockito.mock(DebitWrapper.class);
        Mockito.when(debitWrapper.prepareTransaction(Mockito.anyString())).thenReturn(DEFAULT_TRANS_ID);
        Mockito.when(debitWrapper.getStatus()).thenReturn(TransactionStatus.SUCCESS);
    }

    private void setCreditPrepareSuccess() {
        this.creditWrapper = Mockito.mock(CreditWrapper.class);
        Mockito.when(creditWrapper.prepareTransaction(Mockito.anyString())).thenReturn(DEFAULT_TRANS_ID);
        Mockito.when(creditWrapper.getStatus()).thenReturn(TransactionStatus.SUCCESS);
    }

    private void setDebitPrepareFailure() {
        this.debitWrapper = Mockito.mock(DebitWrapper.class);
        Mockito.when(debitWrapper.prepareTransaction(Mockito.anyString())).thenThrow(new TransactionFailure(Error.DEBIT_FAILED));
        Mockito.when(debitWrapper.getStatus()).thenReturn(TransactionStatus.FAIL);
    }

    private void setCreditPrepareFailure() {
        this.creditWrapper = Mockito.mock(CreditWrapper.class);
        Mockito.when(creditWrapper.prepareTransaction(Mockito.anyString())).thenThrow(new TransactionFailure(Error.CREDIT_FAILED));
        Mockito.when(creditWrapper.getStatus()).thenReturn(TransactionStatus.FAIL);
    }

    private void setDebitCommitSuccess() {
        this.debitWrapper = Mockito.mock(DebitWrapper.class);
        Mockito.when(debitWrapper.commit(Mockito.anyString(), Mockito.anyString())).thenReturn(DEFAULT_TRANS_ID);
        Mockito.when(debitWrapper.getStatus()).thenReturn(TransactionStatus.SUCCESS);
    }

    private void setCreditCommitSuccess() {
        this.creditWrapper = Mockito.mock(CreditWrapper.class);
        Mockito.when(creditWrapper.commit(Mockito.anyString(), Mockito.anyString())).thenReturn(DEFAULT_TRANS_ID);
        Mockito.when(creditWrapper.getStatus()).thenReturn(TransactionStatus.SUCCESS);
    }

    private void setDebitCommitFailure() {
        this.debitWrapper = Mockito.mock(DebitWrapper.class);
        Mockito.when(debitWrapper.commit(Mockito.anyString(), Mockito.anyString())).thenThrow(new TransactionFailure(Error.DEBIT_FAILED));
        Mockito.when(debitWrapper.getStatus()).thenReturn(TransactionStatus.FAIL);
    }

    private void setCreditCommitFailure() {
        this.creditWrapper = Mockito.mock(CreditWrapper.class);
        Mockito.when(creditWrapper.commit(Mockito.anyString(), Mockito.anyString())).thenThrow(new TransactionFailure(Error.CREDIT_FAILED));
        Mockito.when(creditWrapper.getStatus()).thenReturn(TransactionStatus.FAIL);
    }
}