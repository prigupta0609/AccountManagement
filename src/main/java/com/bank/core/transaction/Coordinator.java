package com.bank.core.transaction;

import com.bank.core.Error;
import com.bank.core.transaction.exception.TransactionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Coordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Coordinator.class);
    private String currentPayeeTransID = "-1";
    private String currentReceiverTransID = "-1";

    Coordinator() {
    }

    public TransactionStatus startTransaction (ITransactionWrapper creditWrapper, ITransactionWrapper debitWrapper, String requestID) throws TransactionFailure {
        Future<String> payeeFuture = null;
        Future<String> receiverFuture = null;
        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            payeeFuture = executor.submit(new Callable<String>() {
                                          public String call() {
                                              return debitWrapper.prepareTransaction(requestID);
                                          }});
            receiverFuture = executor.submit(new Callable<String>() {
                                        public String call() {
                                            return creditWrapper.prepareTransaction(requestID);
                                        }});
            executor.shutdown();
            currentPayeeTransID = payeeFuture.get(5, TimeUnit.SECONDS);
            currentReceiverTransID = receiverFuture.get(5, TimeUnit.SECONDS);
            LOGGER.info("Transaction prepared for request " + requestID);
            return TransactionStatus.SUCCESS;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e.getCause().getClass().equals(TransactionFailure.class)) {
                handleTransactionFailureForPrepare ((TransactionFailure) ((ExecutionException) e).getCause(), debitWrapper, creditWrapper, requestID);
            }
            LOGGER.error("Transaction timed out for request " + requestID);
            e.printStackTrace();
        }
        return TransactionStatus.FAIL;
    }

    private void handleTransactionFailureForPrepare (TransactionFailure failure, ITransactionWrapper debitWrapper, ITransactionWrapper creditWrapper, String requestID) throws TransactionFailure{
        failure.printStackTrace();
        if (!failure.getMessage().equals(Error.INSUFFICIENT_BALANCE_ERROR.getErrorMessage())) {
            if (debitWrapper.getStatus().equals(TransactionStatus.FAIL) && creditWrapper.getStatus().equals(TransactionStatus.SUCCESS)) {
                rollback(creditWrapper, currentReceiverTransID, requestID);
            } else if (debitWrapper.getStatus().equals(TransactionStatus.SUCCESS) && creditWrapper.getStatus().equals(TransactionStatus.FAIL)) {
                rollback(debitWrapper, currentPayeeTransID, requestID);
            }
        }
        throw failure;
    }

    private String rollback (ITransactionWrapper wrapper, String transactionID, String requestID) throws TransactionFailure {
        String resultTransID = "-1";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            public String call() {
                return wrapper.rollback(transactionID, requestID);
            }});
        executor.shutdown();
        try {
            resultTransID = future.get(5, TimeUnit.SECONDS);
            return resultTransID;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Unable to rollback operation, move to queue for manual intervention for transactionID : " + resultTransID);
            if (e.getCause().getClass().equals(TransactionFailure.class)) {
                throw (TransactionFailure) ((ExecutionException) e).getCause();
            }
            e.printStackTrace();
        }
        return transactionID;
    }

    public TransactionStatus commit(ITransactionWrapper receiver, ITransactionWrapper payee, String requestID) throws TransactionFailure {
        TransactionStatus payeeResult = null;

        payeeResult = commitBlockingTransaction(payee, 5, TimeUnit.SECONDS, currentPayeeTransID, requestID);
        if (payeeResult.equals(TransactionStatus.SUCCESS)) {
            TransactionStatus receiverResult = commitBlockingTransaction(receiver, 5, TimeUnit.SECONDS, currentReceiverTransID, requestID);
            if (!receiverResult.equals(TransactionStatus.SUCCESS)) {
                currentReceiverTransID = rollback(receiver, currentReceiverTransID, requestID);
                currentPayeeTransID = rollback(payee, currentPayeeTransID, requestID);
                return TransactionStatus.FAIL;
            } else {
                return TransactionStatus.SUCCESS;
            }
        } else {
            rollback(payee, currentPayeeTransID, requestID);
            return TransactionStatus.FAIL;
        }
    }

    private TransactionStatus commitBlockingTransaction(ITransactionWrapper wrapper, long timeout, TimeUnit unit, String transactionID, String requestID) throws TransactionFailure {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            public String call() {
                return wrapper.commit(transactionID, requestID);
            }});
        executor.shutdown();
        try {
            future.get(timeout, unit);
            if (wrapper.getStatus().equals(TransactionStatus.SUCCESS)) {
                return TransactionStatus.SUCCESS;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e.getCause().getClass().equals(TransactionFailure.class)) {
                throw (TransactionFailure) ((ExecutionException) e).getCause();
            }
            LOGGER.error("Unable to commit operation, move to queue for manual intervention for requestID : " + requestID);
            e.printStackTrace();
        }
        return TransactionStatus.FAIL;
    }
}
