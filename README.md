# Processo Seletivo - Pismo

This project is part of the selection process of Pismo company.
The project consist of two intercommunicating microservices.
The first microservice is an Account API and the second is a Transaction API.
Each microservice has a database and they share the id (for Account API) or account_id (for Transaction API) field.
The database of Account API project ('accountdb') has one Collection ("Account.class").
The database of Transaction API project ('db') has two Collection's ("Transaction.class" and "Payment.class").
Each payment related to an account can generate a payment tracking, which is persisted into 'db' database under Collection "Payment.class".
Every transaction is persisted into "Transaction.class" Collection and every account into "Account.class" Collection.
Communication occurs in Account API project, since this API depends on information presented in Transaction API database.
I chose Spring Boot framework, because it uses Java and I am really familiar with it.
Since the project has two stand-alone API's, a framework which implements the microservice architecture is the best approach.
Therefore, I chose MongoDB, which is a non-relational database (NoSQL) and it is more appropriate for applications that manage large amount of information and require structure flexibility as payment management applications do.


## Getting Started

1. Open the two projects on IntelliJ or any other IDE.
2. Build project using Maven (do not forget to add dependency of Transaction API project on Account API project).
3. All other dependencies are already configured.
4. Add run configuration for each project. 
5. Run project Account API to add some accounts.
6. Both databases are executed in different ports: 'accountdb': 8080 and 'db': 8081
7. Open Postman and add some accounts in JSON format, using POST HTTP method (http://localhost:8080/accounts).
8. Open Robo 3T and create a new connection for MongoDB.
9. You will see that a collection has been created for the "Account.class" in 'accountdb' database.
10. Now, run Transaction API project and add some transactions.
11. Open Postman again and add some transactions in JSON format, using POST HTTP method (http://localhost:8081/transactions).
12. You will see that a collection has been created for the "Transaction.class" in 'db' database on Robo 3T.
13. Use PATCH HTTP method (http://localhost:8081/payments/tracking/{account_id}) to execute calculations.
14. You will see that another collection has been created for the "Payment.clas" in "db" database on Robo 3T.
15. Now you can go back to port 8080 and use PATCH HTTP method (http://localhost:8080/accounts/{id}).
16. Go back to Robo 3T application and you will see that the Account.class collection has been updated.


### Prerequisites

1. IDE (Eclipse, IntelliJ, ...)
2. Maven
3. Spring Boot (version 1.5.4)
4. MongoDB
5. MongoDB Driver (version 3.4.3)
6. Morphia (version 1.2.0)
7. Postman (Google extension plugin)
8. Robo 3T

##Installation

To install MongoDB in your computer, execute the following commands on terminal:


1. Create a directory /data/db/ anywhere in your computer and go to this directory and execute: $ brew install mongod
2. Go to the MongoDB installation directory and execute: $ export PATH=<MONGODB_INSTALLATION_PATH>/bin:$PATH
3. Then, in the same directory, execute: $ mongod --dbpath <ANY_PATH>/data/db

## Running the tests

For each project, there is a "test" folder under "src" folder.
There, you can find all the tests related to each collection in both databases.
You can run the test by clicking the right-button on the test file and choosing "Run" (on IntelliJ, for instance).



## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring Boot](https://projects.spring.io/spring-boot/) - The framework used
* [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/) - The Java driver used
* [Morphia](https://mongodb.github.io/morphia/) - The Java Object Document Mapper for MongoDB


## Authors

* **Erica Okamura** - [ericaokamura](https://github.com/ericaokamura/)


## Acknowledgments

* My parents
* Friends

