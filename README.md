###To build service###
    mvn clean install
    
###To run service###
    java -jar target/account-management-1.0-SNAPSHOT.jar server application.yml
     
NOTES:
1. Dummy database is stored in data.yml
2. Sample json request location - AccountManagement/src/test/resources/TransferRequest.json
3. Thread Group.jmx is added for multi-threaded testing.

Assumptions:
1. Accounts are in same bank.
2. Handles single currency only.
3. Account validation or user authentication is not taken care of here.
4. Only one type (saving/current) of account exists.

Actual implementation scenarios:
1. Account information should be stored in encrypted manner in database in order to prevent security breach.
2. Exchange rate and other obligations will be applied for inter-currency.
3. Account transfer in crypto currency and wallets etc should be provided.
4. Application and system level metrics should be pushed to monitoring tools for debugging.
5. Tokens should be used to send secure information.
6. HTTPS connection should be used.