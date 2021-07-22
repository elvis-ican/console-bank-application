package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * The AccountDao interface declares some basic methods for manipulating account
 * data in the database.
 */
public interface AccountDao {
    public int addAccount(String userId, Account account);
    public Account getAccount(String accountNumber);
    public List<String> getAccountNumbers(String userId);
    public int deposit(String accountNumber, BigDecimal amount);
    public int withdraw(String accountNumber, BigDecimal amount);
    public int transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
}
