package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.AccountType;
import dev.elvislee.revature.project.util.ConnectionUtil;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The AccountDaoImpl class provides various methods
 * for saving, retrieving and updating account related data
 * into or from the database.
 */
public class AccountDaoImpl implements AccountDao{
    private Logger logger = Log4j.getLogger();
    private static AccountDaoImpl accountDao;

    private AccountDaoImpl() {
    }

    /**
     * The getAccountDaoInstance method ensure a singleton
     * accountDao is returned to the methode invoked it.
     */
    public static AccountDaoImpl getAccountDaoInstance() {
        if (accountDao == null) {
            accountDao = new AccountDaoImpl();
        }
        return accountDao;
    }

    /**
     * The addAccount method takes a userId and an account object,
     * the data of the account object will be extracted and add into
     * the account and user_account tables in the database.
     *
     * @param   userId
     * @param   account
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    public int addAccount(String userId, Account account) {
        if (userId != null && account != null) {
            String acNum = getNextAccountNumber();
            String acType;
            if (account.getAccountType() == AccountType.SAVING) {
                acType = "saving";
            } else if (account.getAccountType() == AccountType.CHECKING) {
                acType = "checking";
            } else {
                acType = "joint";
            }
            BigDecimal balance = BigDecimal.ZERO;
            String sql1 = "insert into account values(?,?,?)";
            String sql2 = "insert into user_account values(?,?)";
            int count1 = 0;
            int count2 = 0;
            Connection conn = null;
            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            try {
                conn = ConnectionUtil.getConnection();
                conn.setAutoCommit(false);
                ps1 = conn.prepareStatement(sql1);
                ps1.setString(1, acNum);
                ps1.setString(2, acType);
                ps1.setBigDecimal(3, balance);
                count1 = ps1.executeUpdate();
                ps2 = conn.prepareStatement(sql2);
                ps2.setString(1, acNum);
                ps2.setString(2, userId);
                count2 = ps2.executeUpdate();
                if (count1 != 1 || count2 != 1) {
                    conn.rollback();
                } else {
                    conn.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException ex) {
                    logger.error(ex);
                    e.printStackTrace();
                }
            } finally {
                try {
                    ps1.close();
                    ps2.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return count1;
        }
        return 0;
    }

    /**
     * The addAccount method takes two userIds (for Joint Account creation)
     * and an account object, the data of the account object will be extracted
     * and add into the account and user_account tables in the database.
     *
     * @param   userId1
     * @param   userId2
     * @param   account
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    public int addAccount(String userId1, String userId2, Account account) {
        if (userId1 != null && userId2 != null && account != null) {
            String acNum = getNextAccountNumber();
            String acType;
            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            if (account.getAccountType() == AccountType.SAVING) {
                acType = "saving";
            } else if (account.getAccountType() == AccountType.CHECKING) {
                acType = "checking";
            } else {
                acType = "joint";
            }
            BigDecimal balance = BigDecimal.ZERO;
            String sql1 = "insert into account values(?,?,?)";
            String sql2 = "insert into user_account values(?,?)";
            Connection conn = null;
            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            try {
                conn = ConnectionUtil.getConnection();
                conn.setAutoCommit(false);
                ps1 = conn.prepareStatement(sql1);
                ps2 = conn.prepareStatement(sql2);
                ps1.setString(1, acNum);
                ps1.setString(2, acType);
                ps1.setBigDecimal(3, balance);
                count1 = ps1.executeUpdate();
                ps2.setString(1, acNum);
                ps2.setString(2, userId1);
                count2 = ps2.executeUpdate();
                ps2.setString(1, acNum);
                ps2.setString(2, userId2);
                count3 = ps2.executeUpdate();
                if (count1 != 1 || count2 != 1 || count3 != 1) {
                    conn.rollback();
                } else {
                    conn.commit();
                }
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException e1) {
                    logger.error(e1);
                    e.printStackTrace();
                }
            } finally {
                try {
                    ps1.close();
                    ps2.close();
                    conn.close();
                } catch (SQLException e2) {
                    logger.error(e2);
                    e2.printStackTrace();
                }
            }
            return count1;
        }
        return 0;
    }

    /**
     * The getAccountNumbers method takes a userId to retrieve all
     * account numbers under the user, and return the list of the
     * account numbers.
     *
     * @param   userId
     * @return
     */
    @Override
    public List<String> getAccountNumbers(String userId) {
        List<String> list = new ArrayList<>();
        String sql = "select ac_number from user_account where userid=?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("ac_number"));
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * The getAccount method takes an accountNumber, retrieving the information
     * of the account, create and return the Account object.
     *
     * @param   accountNumber
     * @return
     */
    @Override
    public Account getAccount(String accountNumber) {
        if (accountNumber == null ) {
            return Account.NULL_ACCOUNT;
        }
        Account account = new Account();
        String acType = "undefined";
        BigDecimal balance = BigDecimal.ZERO;
        List<String> userIds = new ArrayList<>();
        String sql = "select ac_type, balance, userid from account, user_account ua "
               + "where account.ac_number = ua.ac_number and account.ac_number=?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                balance = rs.getBigDecimal("balance");
                acType = rs.getString("ac_type");
                userIds.add(rs.getString("userid"));
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        if (!acType.equals("undefined")) {
            account.setAccountNumber(accountNumber);
            if (acType.equals("saving")) {
                account.setAccountType(AccountType.SAVING);
            } else if (acType.equals("checking")) {
                account.setAccountType(AccountType.CHECKING);
            } else {
                account.setAccountType(AccountType.JOINT);
            }
            account.setBalance(balance);
            account.setUserIds(userIds);
            return account;
        }
        return Account.NULL_ACCOUNT;
    }

    /**
     * The getSavingCheckingAccount method takes a userId, the account type, retrieving the information
     * of the account, create and return the Account object.
     *
     * @param   userId
     * @param   accountType
     * @return
     */
    public Account getAccount(String userId, AccountType accountType) {
        if (userId == null || accountType == null) {
            return Account.NULL_ACCOUNT;
        }
        String acType = accountType == AccountType.SAVING ? "saving" : "checking";
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        String accountNumber = null;
        BigDecimal balance = BigDecimal.ZERO;
        String sql = "select a.ac_number, balance from account a, user_account ua, bank_user bu " +
                "where ua.ac_number = a.ac_number and a.ac_type=? and ua.userid = bu.userid and bu.userid =?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acType);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                balance = rs.getBigDecimal("balance");
                accountNumber = rs.getString("ac_number");
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        if (accountNumber != null) {
            Account account = new Account();
            account.setAccountNumber(accountNumber);
            account.setBalance(balance);
            account.setAccountType(accountType);
            account.setUserIds(userIds);
            return account;
        }
        return Account.NULL_ACCOUNT;
    }

    /**
     * The deposit method takes an account number and the amount,
     * updates the balance of the associated account by adding the amount to
     * the original balance.
     *
     * @param   accountNumber
     * @param   amount
     * @return
     */
    public int deposit(String accountNumber, BigDecimal amount) {
        if (accountNumber == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        int count = 0;
        Account account = getAccount(accountNumber);
        if (account != Account.NULL_ACCOUNT) {
            BigDecimal balance = account.getBalance().add(amount);
            String sql = "update account set balance=? where ac_number=?";
            try (Connection conn = ConnectionUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, balance);
                ps.setString(2, accountNumber);
                count = ps.executeUpdate();
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * The withdraw method takes an account number and the amount,
     * updates the balance of the associated account by deducting the amount from
     * the original balance.
     *
     * @param   accountNumber
     * @param   amount
     * @return
     */
    public int withdraw(String accountNumber, BigDecimal amount) {
        if (accountNumber == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        int count = 0;
        Account account = getAccount(accountNumber);
        if (account != Account.NULL_ACCOUNT) {
            BigDecimal originalBalance = account.getBalance();
            if (originalBalance.compareTo(amount) >= 0) {
                BigDecimal balance = account.getBalance().subtract(amount);
                String sql = "update account set balance=? where ac_number=?";
                try (Connection conn = ConnectionUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setObject(1, balance);
                    ps.setString(2, accountNumber);
                    count = ps.executeUpdate();
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            }
        }
        return count;
    }

    /**
     * The deposit method takes two account numbers and the amount,
     * updates the balances of the associated accounts by deduction the amount
     * from the fromAccount and adding the amount to the toAccount. Both transactions
     * are grouped into one transaction with rollback if it cannot complete the whole
     * transaction.
     *
     * @param   fromAccountNumber
     * @param   toAccountNumber
     * @param   amount
     * @return
     */
    public int transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        int countFrom = 0;
        int countTo = 0;
        Account accountFrom = getAccount(fromAccountNumber);
        Account accountTo = getAccount(toAccountNumber);
        if (accountFrom != Account.NULL_ACCOUNT && accountTo != Account.NULL_ACCOUNT) {
            BigDecimal oriBalanceFrom = accountFrom.getBalance();
            if (oriBalanceFrom.compareTo(amount) >= 0) {
                BigDecimal balanceFrom = accountFrom.getBalance().subtract(amount);
                BigDecimal balanceTo = accountTo.getBalance().add(amount);
                String sql = "update account set balance=? where ac_number=?";
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                    conn = ConnectionUtil.getConnection();
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    ps.setObject(1, balanceFrom);
                    ps.setString(2, fromAccountNumber);
                    countFrom = ps.executeUpdate();
                    ps.setObject(1, balanceTo);
                    ps.setString(2, toAccountNumber);
                    countTo = ps.executeUpdate();
                    if (countFrom != 1 || countTo != 1) {
                        conn.rollback();
                    } else {
                        conn.commit();
                    }
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                    try {
                        if (conn != null) {
                            conn.rollback();
                        }
                    } catch (SQLException e1) {
                        logger.error(e1);
                        e1.printStackTrace();
                    }
                } finally {
                    try {
                        ps.close();
                        conn.close();
                    } catch (SQLException e2) {
                        logger.error(e2);
                        e2.printStackTrace();
                    }
                }
            }
        }
        return countFrom;
    }

    /**
     * The getNextAccountNumber method read the next account number from the
     * next_account_number table in the database for assigning it to a newly
     * created account. It will then update the record by increment the value
     * by one for next account.
     *
     * @return
     */
    public String getNextAccountNumber() {
        String acNum = null;
        String sql1 = "select * from next_account_number";
        try (Connection conn = ConnectionUtil.getConnection();
             Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql1);
            if (rs.next()) {
                acNum = rs.getString("next_ac_number");
            }
            String nextAcNum = String.valueOf(Integer.parseInt(acNum) + 1);
            String sql2 = "update next_account_number set next_ac_number='" + nextAcNum + "'";
            statement.executeUpdate(sql2);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return acNum;
    }
}
