# To build service

    mvn clean install
    
# To run service

    java -jar target/account-management-1.0-SNAPSHOT.jar server application.yml

# To run functional test

    mvn surefire:test -Dtest=FunctionalTest    
     
# NOTES:
1. Dummy database is stored in data.yml
2. Sample json request location - AccountManagement/src/test/resources/TransferRequest.json
3. Thread Group.jmx is added for multi-threaded testing.

# Assumptions:
1. Accounts are in same bank.
2. Handles single currency only.
3. Account validation or user authentication is not taken care of here.
4. Only one type (saving/current) of account exists.

# Actual implementation scenarios:
1. Account information should be stored in encrypted manner in database in order to prevent security breach.
2. Exchange rate and other obligations will be applied for inter-currency.
3. Account transfer in crypto currency and wallets etc should be provided.
4. Application and system level metrics should be pushed to monitoring tools for debugging.
5. Tokens should be used to send secure information.
6. HTTPS connection should be used.

# Design flow
1. When application starts, in-memory database is created using data.yml file.
2. JSON request comes at AccountManagementResource. This request contains information of payee, receiver and the amount to be transferred.
3. Request and the accounts of payee and receiver are validated.
4. Transfer request is now triggered and handled by coordinator. This coordinator will be created for each request.
5. If validation passes, transfer starts with preparing the debit and credit transaction. Preparation involve pre-checks for each operation.
6. Amount is HOLD in preparation of debit operation and READY in credit.
7. New transactions are added in transaction table (object in our case as database is in-memory) for debit and credit.
8. If preparation of both the operations is successful, commit operation is triggered.
9. In commit operation, amount is deducted from payee account and credited to receiver account.
10. On each step, new transaction entry will be made which will contain TransactionID and PreviousTransactionID to connect will all the transactions of single request.

# Error scenarios
1. ValidationException occurs if request is invalid or accounts are invalid.
2. If preparation of debit or credit fails, TransactionFailure happens.
3. If commit fails, whole transaction is roll-backed.
4. If error occurs in rollback, TransactionFailure is thrown and the transaction will need manual intervention (manual intervention is not implemented int this as of now).
