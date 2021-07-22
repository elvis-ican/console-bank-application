package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class transactionDaoImplTest {
    private final TransactionDaoImpl transactionDao = TransactionDaoImpl.getTransactionDAOInstance();

    @Test
    public void testAddDepositTransaction() {
        List<String> userIds = new ArrayList<>();
        userIds.add("harry");
        Account account = new Account(userIds, AccountType.SAVING);
        account.setAccountNumber("2023031000");
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, "harry", LocalDateTime.now(),
                BigDecimal.TEN, account);
        assertEquals(1, transactionDao.addTransaction(transaction));
    }

    @Test
    public void testAddWithdrawTransaction() {
        List<String> userIds = new ArrayList<>();
        userIds.add("harry");
        Account account = new Account(userIds, AccountType.SAVING);
        account.setAccountNumber("2023031000");
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, "harry", LocalDateTime.now(),
                account, BigDecimal.TEN);
        assertEquals(1, transactionDao.addTransaction(transaction));
    }

    @Test
    public void testAddTransferTransaction() {
        List<String> userIds = new ArrayList<>();
        userIds.add("harry");
        Account accountFrom = new Account(userIds, AccountType.SAVING);
        accountFrom.setAccountNumber("2023031000");
        Account accountTo = new Account(userIds, AccountType.CHECKING);
        accountTo.setAccountNumber("2023031001");
        Transaction transaction = new Transaction(TransactionType.TRANSFER, "harry", LocalDateTime.now(),
                accountFrom, BigDecimal.TEN, accountTo);
        assertEquals(1, transactionDao.addTransaction(transaction));
    }

    @Test
    public void testGetTransactions() {
        assertEquals(11, transactionDao.getTransactions("harry").size());
    }

    @Test
    public void testGetAccountTransaction() {
        assertEquals(6, transactionDao.getAccountTransaction("2023031000").size());
    }
}
