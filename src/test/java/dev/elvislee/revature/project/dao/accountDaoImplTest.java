package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.AccountType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class accountDaoImplTest {
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();

    @Test
    public void testAddNewAccount() {
        List<String> userIds = new ArrayList<>();
        userIds.add("user1");
        Account account = new Account(userIds, AccountType.SAVING);
        assertEquals(1, accountDao.addAccount("user1", account));
    }

    @Test
    public void testAddNewCheckingAccount() {
        List<String> userIds = new ArrayList<>();
        userIds.add("user1");
        Account account = new Account(userIds, AccountType.CHECKING);
        assertEquals(1, accountDao.addAccount("user1", account));
    }

    @Test
    public void testAddNewAccountToNullUser() {
        Account account = new Account(null, AccountType.SAVING);
        assertEquals(0, accountDao.addAccount(null, account));
    }

    @Test
    public void testAddJointAccount() {
        List<String> userIds = new ArrayList<>();
        userIds.add("harry");
        userIds.add("user1");
        Account account = new Account(userIds, AccountType.JOINT);
        assertEquals(1, accountDao.addAccount("harry", "user1", account));
    }

    @Test
    public void testAddJointAccountWithOneUser() {
        List<String> userIds = new ArrayList<>();
        userIds.add("user1");
        Account account = new Account(userIds, AccountType.JOINT);
        assertEquals(0, accountDao.addAccount("user1", null, account));
    }

    @Test
    public void testGetExistedAccount() {
        assertEquals("2023031000", accountDao.getAccount("2023031000").getAccountNumber());
    }

    @Test
    public void testGetNonExistedAccount() {
        assertEquals(Account.NULL_ACCOUNT, accountDao.getAccount("2222222222"));
    }

    @Test
    public void testGetExistedSavingAccount() {
        assertEquals("2023031000", accountDao.getAccount("harry", AccountType.SAVING).getAccountNumber());
    }

    @Test
    public void testGetExistedCheckingAccount() {
        assertEquals("2023031001", accountDao.getAccount("harry", AccountType.CHECKING).getAccountNumber());
    }

    @Test
    public void testGetNonExistedCheckingAccount() {
        assertEquals(Account.NULL_ACCOUNT, accountDao.getAccount("noOne", AccountType.CHECKING));
    }

    @Test
    public void testDepositToExistedAccount() {
        assertEquals(1, accountDao.deposit("2023031000", BigDecimal.TEN));
    }

    @Test
    public void testDepositNegativeAmount() {
        assertEquals(0, accountDao.deposit("2023031000", BigDecimal.valueOf(-100)));
    }

    @Test
    public void testDepositToNonExistedAccount() {
        assertEquals(0, accountDao.deposit("22222222222", BigDecimal.TEN));
    }

    @Test
    public void testWithdrawFromExistedAccount() {
        assertEquals(1, accountDao.withdraw("2023031000", BigDecimal.TEN));
    }

    @Test
    public void testWithdrawFromNonExistedAccount() {
        assertEquals(0, accountDao.withdraw("2222222222", BigDecimal.TEN));
    }

    @Test
    public void testInsufficientFundWithdraw() {
        assertEquals(0, accountDao.withdraw("2023031000", BigDecimal.valueOf(10000)));
    }

    @Test
    public void testEnoughFundTransfer() {
        assertEquals(1, accountDao.transfer("2023031000", "2023031001", BigDecimal.TEN));
    }

    @Test
    public void testInsufficientFundTransfer() {
        assertEquals(0, accountDao.transfer("2023031000", "2023031001", BigDecimal.valueOf(1000000)));
    }

    @Test
    public void testTransferToNonExistedAccount() {
        assertEquals(0, accountDao.transfer("2023031000", "2222222222", BigDecimal.valueOf(10000)));
    }

    @Test
    public void testGetNextAccountNumber() {
        assertNotNull(accountDao.getNextAccountNumber());
    }

}
