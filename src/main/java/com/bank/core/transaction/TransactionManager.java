package com.bank.core.transaction;

import com.bank.contract.Amount;
import com.bank.contract.Payee;
import com.bank.contract.Receiver;
import com.bank.core.transaction.exception.TransactionFailure;
import com.bank.dao.DAO;
import com.bank.dao.core.Account;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionManager {

    private static TransactionManager INSTANCE = new TransactionManager();

    private static List<Account> cachedAccountList;
    private static DAO dao;

    public static TransactionManager getInstance() {
        return INSTANCE;
    }

    public TransactionStatus transfer(Receiver receiverAccount, Payee payeeAccount, Amount amount, DAO dao) throws TransactionFailure {
        this.dao = dao;
        cachedAccountList = dao.getAccount();
        Account receiverAccountInfo = getAccount(receiverAccount.getAccount());
        if (receiverAccountInfo == null) {

        }
        Account payeeAccountInfo = getAccount(payeeAccount.getAccount());

        CompositeTransactionLogic compositeTransactionLogic = new CompositeTransactionLogic();

        compositeTransactionLogic.register(getDebitWrapper(receiverAccountInfo, payeeAccountInfo, amount),
                getCreditWrapper(receiverAccountInfo, payeeAccountInfo, amount));
        if (compositeTransactionLogic.startTransaction().equals(TransactionStatus.SUCCESS)) {
            return compositeTransactionLogic.commit();
        }
        return TransactionStatus.FAIL;
    }

    private ITransactionWrapper getCreditWrapper(Account receiverAccount, Account payeeAccount, Amount amount) {
        CreditWrapper creditWrapper = new CreditWrapper.CreditWrapperBuilder()
                .withAccountNumber(payeeAccount)
                .withAmount(amount)
                .withReceiverAccount(receiverAccount)
                .withDAO(dao)
                .build();
        return creditWrapper;
    }

    private ITransactionWrapper getDebitWrapper(Account receiverAccount, Account payeeAccount, Amount amount) {
        DebitWrapper debitWrapper = new DebitWrapper.DebitWrapperBuilder()
                .withAccountNumber(payeeAccount)
                .withAmount(amount)
                .withReceiverAccount(receiverAccount)
                .withDAO(dao)
                .build();
        return debitWrapper;
    }

    private Account getAccount (com.bank.contract.Account account) {
        return cachedAccountList.stream().filter(x -> x.getNumber().equals(account.getAccountNumber())).findFirst().orElse(null);
    }
}
