package dev.elvislee.revature.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The Transaction class helps to create a POJO transaction object for storing
 * information of the transaction and pass between methods. Most of the fields
 * of the transaction class are same as the corresponding transaction table in the
 * database.
 */
public class Transaction {
    private TransactionType transactionType;
    private String userId;
    private LocalDateTime dateTime;
    private BigDecimal amount;
    private Account from = Account.NULL_ACCOUNT;
    private Account to = Account.NULL_ACCOUNT;
    public Transaction() {
    }

     // Constructor for transfer transaction
    public Transaction(TransactionType transactionType, String userId, LocalDateTime dateTime, Account from, BigDecimal amount,  Account to) {
        this.transactionType = transactionType;
        this.userId = userId;
        this.dateTime = dateTime;
        this.amount = amount;
        this.from = from;
        this.to = to;
    }


     // Constructor for withdraw transaction
    public Transaction(TransactionType transactionType, String userId, LocalDateTime dateTime, Account from, BigDecimal amount) {
        this.transactionType = transactionType;
        this.userId = userId;
        this.amount = amount;
        this.from = from;
        this.dateTime = dateTime;
    }


     // Constructor for deposit transaction

    public Transaction(TransactionType transactionType, String userId, LocalDateTime dateTime, BigDecimal amount, Account to) {
        this.transactionType = transactionType;
        this.userId = userId;
        this.amount = amount;
        this.to = to;
        this.dateTime = dateTime;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
