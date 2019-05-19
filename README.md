Money Transfer App With SparkJava
===========
This Money Transfer application contains all the necessary `api` for
money transfer between accounts. User can open an `account` with an
initial deposit amount, an existing account holder can `deposit` or
`withdraw` amount which will instantly reflect on the account balance.
The apis provides the flexibility of money `transfer` between two
accounts in either `immediate` or `schedule` mode. The immediate
transfer will update the transfer instantly whereas scheduled will
update in a schedule table which can be processed later via a batch job
. For the scheduled transfer, the `api` and `table` structure is
provided whereas batch job is not implemented in the scope of project.
The `apis` are implemented with Test Driven Development and covered by
unit and integration test with concurrency.

How start the application
===========

- Clone the project from repo at git clone https://github.com/sujittripathy/sparkjava-demo-money-transfer.git
- Navigate to source root folder `sparkjava-demo-money-transfer`
- Execute `./mvnw clean package`. This will create the fat jar at `/target` folder.
- Navigate to `/target` folder and execute
`java -jar sparkjava-demo-money-transfer-1.0.jar`
- This will take few seconds to start the application.
```
[Thread-1] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@2e2a0535{HTTP/1.1,[http/1.1]}{0.0.0.0:4567}
[Thread-1] INFO org.eclipse.jetty.server.Server - Started @694ms
```
- App will start at port `4567` and the apis can be accessed via
  `http://localhost:4567/<api path>`

Tech Stack
===========

|Framework|Description|    
|--|--|
|Java 8||
|Maven|Maven shade plugin for fat jar|
|SparkJava|A micro web framework|
|Gson|Json serde library|
|H2|In momory database|
|Jdbi|A lightweight framework for DB abstraction|
|Guice|For Dependency Injection|
|Lombok|For java boilerplate replacemenet|
|Unirest|a thin HTTP library used for testing purpose|
|sl4j|For logging|
|junit, mockito|For unit test|
|Assertj|assertions framework|



Design Considerations
=======================

- The app is packaged as a fat jar so, it can execute as a standalone
  program and all necessary libraries, web server, in-memory database is
  bundled.
- The code is being designed and developed as modular and dependency
  injection so, any future changes can be done with minimum fuss.
- APIs are designing with REST best practices with consideration of
  `versioning` and appropriate usage of verb and noun.
- Transactions (transfer, deposits) logic is implemented with consideration of `concurrency`  and `synchronization`.
- Exception handling is being considered for data validations as well as any service or business level validations. Exception returns generic json as response.
- Pagination is not considered while fetching all accounts via `account/all` API.
- The currency is only considered only with `USD` without any exchange
  rate calculation however, the application can be extended for
  multi-currency support.
- App is tested with concurrency/multi-threaded executors however, not
  tested with any kind of load testing tools.
- Currently the `lock` is instantiated on class level (`MoneyTransferDAOImpl`) which locks the transaction,
however an alternate approach could be to be on the account level and as a
`ConcurrencyHashMap` to keep the lock object and shared among the calls.

API Details
===========


## Account ##

**Add a new user** - ```POST``` - ```http://localhost:4567/v1/account/add```
 
**Fetch all account details** - ```GET``` -
```http://localhost:4567/v1/account/all```

**Fetch details on a single account** - ```GET``` -
```http://localhost:4567/v1/account/100000```

**Make a deposit** - ```POST``` -
```http://localhost:4567/v1/account/deposit```
 
**Make a withdraw** - ```POST``` -
```http://localhost:4567/v1/account/withdraw```
 
**Close account** - ```PUT``` -
```http://localhost:4567/v1/account/100000/close```

## Transfer ##

**Make a transfer** - ```POST``` -
```http://localhost:4567/v1/transfer/immediate```

**Make a scheduled transfer** - ```POST``` -
```http://localhost:4567/v1/transfer/schedule```
 
**Get details from an existing transfer** - ```GET``` -
```http://localhost:4567/v1/transfer/10000002```

The more details on above APIs can be found at
https://github.com/sujittripathy/sparkjava-demo-money-transfer/wiki/Money-Transfer-API-Documentation

Database Design
==============

H2 in-memory database is being used for the application. The database
`ACCOUNT` , `TRANSFER` and `TRANSFER_SCHEDULE` tablesto keep the details
of customer accounts and their transfer history. The database also
stores an `unique transaction id` for every transfer for future
tracking. For auto generation of primary key `sequence` is being used in
both the tables. 

Table design follows below:

**ACCOUNT**

| Column Name | Data Type | Length|Description|
|--|--|--|--|
|ID (Primary Key)|BIGINT| Backed by sequence||
|ALIAS|VARCHAR|100|Account Nickname|
|TYPE|VARCHAR|20|Type of account, Checking or Savings|
|BALANCE|DECIMAL|(10,2)|Account balance|
|CURRENCY|VARCHAR|3|account currency|
|OPEN_DATE|DATE|NA|The date account is opened|
|STATUS|VARCHAR|10|Status of the account such as ACTIVE|


**TRANSFER**

| Column Name | Data Type | Length|Description|
|--|--|--|--|
|ID (Primary Key)|BIGINT|Backed by sequence||
|FROM_ACCT (Foreign Key on ID)|BIGINT||On Account Table|
|TO_ACCT (Foreign Key on ID)|BIGINT||On Account Table|
|AMOUNT|DECIMAL|(10,2)|Transfer amount|
|CURRENCY|VARCHAR|3||
|TRANS_DATE|DATE|NA|Transfer Date|
|TRANS_ID|VARCHAR|10|An unique transaction id|
|STATUS|VARCHAR|10|status of the transaction|

**TRANSFER_SCHEDULE**

| Column Name | Data Type | Length|Description|
|--|--|--|--|
|ID (Primary Key)|BIGINT|Backed by sequence||
|FROM_ACCT (Foreign Key on ID)|BIGINT||On Account Table|
|TO_ACCT (Foreign Key on ID)|BIGINT||On Account Table|
|AMOUNT|DECIMAL|(10,2)|schedule transfer amount|
|CURRENCY|VARCHAR|3||
|SCH_TRANSFER_DATE|DATE|NA|Schedule transfer date|
|TRANS_ID|VARCHAR|10|An unique transaction id|
|STATUS|VARCHAR|10|status of the transaction, as SCHEDULED|


Error Handling
=============
- The data validation error code starts with 400
- The transaction error starts with 402 
- Other server run time exception is 500
 
Below are the two sample error scenario response.
 
```
{
    "errorCode": 400,
    "message": "Account balance can't be negative at open",
    "type": "DATA_VALIDATION"
}
```

```
{
    "errorCode": 402,
    "message": "Transfer amount exceeds available balance",
    "type": "AMOUNT_OVERDRAWN"
}
```

Error Code Details :

| Error Code   | Type| Error Detail|
|--|--|--|
| 400 | DATA_VALIDATION|Account format is not valid|
| 400 | DATA_VALIDATION|From and To account can't be the same|
| 400 | DATA_VALIDATION|Input date format is not valid|
| 400 | DATA_VALIDATION|Scheduled transfer date cannot past|
| 400 | DATA_VALIDATION|Account balance can't be negative at open|
| 400 | DATA_VALIDATION|Account type should be Checking or Savings|
| 400 | DATA_VALIDATION|Transfer amount can't be negative|
| 402 | TRANSACTION_ERROR|Exception while performing the transaction|
| 402 | TRANSACTION_ERROR|Unable to close the account|
| 402 | TRANSACTION_ERROR|Transfer amount exceeds available balance|
| 404 | TRANSACTION_ERROR|Account doesn't exists|
| 5XX |GENERIC_EXCEPTION| Any Server side exception||

Testing
==========
- The unit testing code is available at
  ```test\java\com\demo\unittest``` folder
- The integration test is available at
  ```test\java\com\demo\integrationtest``` folder
- The integration test is implemented by starting of the spark server
  and verifying the API communication via Unirest client. 
