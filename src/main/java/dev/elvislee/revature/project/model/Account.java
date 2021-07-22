package dev.elvislee.revature.project.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Account class helps to create a POJO account object for storing
 * information of the account and pass between methods. Most of the fields
 * of the account class are same as the corresponding account table in the
 * database. The List<String> userIds field helps related the account to the
 * user same as the function of the joint table user_account in the database.
 */
public class Account {
    private String accountNumber;
    private List<String> userIds;
    private AccountType accountType;
    private BigDecimal balance = BigDecimal.ZERO;
    public static final Account NULL_ACCOUNT = new Account();

    public Account() {
    }

    public Account(List<String> userIds, AccountType accountType) {
        this.userIds = userIds;
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userId) {
        this.userIds = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
