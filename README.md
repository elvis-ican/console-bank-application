# BANK APPLICATION
### BY ELVIS lEE

## Introduction

BankApp is a console-based banking application.  It was written in Java with data storing in the AWS PostgreSQl server. It provides various functions for user to use common bank services such as deposit, withdraw and transfer.

## Major Services Provided

> User can
> * REGISTER as a new user
> * LOGIN with userId and password
> * CREATE saving account, checking account and joint account
> * DEPOSIT funds into accounts
> * TRANSFER money between accounts
> * WITHDRAW funds from accounts
> * VIEW the balance of accounts
> * VIEW the transaction history for accounts
> * LOGOUT the application

## Installation

The repository contains all the source files, clone or download them. Create an executable JAR files by various IDE such as Intellij, Eclipse, or using Maven "mvn package" command. The JAR file created can be executed in any machine that installed JRE.

## Design Ideas

Though the application is a console-based one, a ConsoleHelper class has being created to handle all interactions with user, aiming at providing a more user-friendly experience. Also, besides of enhancing reusability, the ConsoleHelper class decouples the view from the business logic of the application. It is only required to create a new ConsoleHelper class with suitable static methods, or a view layer with suitable classes for a different view environment such as a kiosk or a web browser.

Since the application requests user to invoke various operations by pressing a number representing it, the Command Design Pattern has been adopted. All classes providing service operations implement Command interface. Command decouples the object that invokes the operation from the one that knows how to perform it. To achieve this separation, a CommandExecutor class has been created for mapping with receiver (Services Command Classes) with user action (inputting a number). The executor class contains an execute method that simply call the action of the receiver, that's achieving runtime polymorphism.

## To-Do

The following things would be nice to do:
* Refactor some methods to make them more easy to test, and extract the common parts to create new reusable methods.
* Set a view to administrator to do some basic settings, such as column width, withdraw and transfer limit, logging history, etc.
* Add a function to check the idle time and forcing logout if it exceeds like fifteen minutes to improve the security.
* Make a web-based user interface for user.

## License

The BankAPP project was as a practice of programming using Java, online PostgreSql database, JDBC, Junit, log4j with Maven build tool. Feel free to use it and make contribution.