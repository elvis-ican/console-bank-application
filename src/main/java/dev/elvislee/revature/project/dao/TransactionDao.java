package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.Transaction;

import java.util.List;

/**
 * The TransactionDao interface declares some basic methods for manipulating
 * transaction data in the database.
 */
public interface TransactionDao {
    public int addTransaction(Transaction transaction);
    public List<Transaction> getTransactions(String userId);
}
