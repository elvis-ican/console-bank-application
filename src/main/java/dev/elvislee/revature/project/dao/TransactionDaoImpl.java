package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.Transaction;
import dev.elvislee.revature.project.model.TransactionType;
import dev.elvislee.revature.project.util.ConnectionUtil;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The TransactionDaoImpl class provides various methods
 * for creating and retrieving transaction records
 * into or from the database.
 */
public class TransactionDaoImpl implements TransactionDao {
    private static TransactionDaoImpl transactionDao;
    private static Logger logger = Log4j.getLogger();

    private TransactionDaoImpl() {
    }

    /**
     * The getTransactionDaoInstance method ensure a singleton
     * transactionDao is returned to the methode invoked it.
     */
    public static TransactionDaoImpl getTransactionDAOInstance() {
        if (transactionDao == null) {
            transactionDao = new TransactionDaoImpl();
        }
        return transactionDao;
    }

    /**
     * The addTransaction method takes a transaction object,
     * the data of the object will be extracted and add into
     * the transaction tables in the database.
     *
     * @param   transaction
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    @Override
    public int addTransaction(Transaction transaction) {
        int count = 0;
        if (transaction == null) {
            return count;
        }
        TransactionType type = transaction.getTransactionType();
        String transType = type == TransactionType.DEPOSIT ? "deposit" : type == TransactionType.WITHDRAW ?
                "withdraw" : "transfer";
        String userId = transaction.getUserId();
        String acNumFrom = transaction.getFrom().getAccountNumber();
        String acNumTo = transaction.getTo().getAccountNumber();
        LocalDateTime dateTime = transaction.getDateTime();
        BigDecimal amount = transaction.getAmount();
        String sql = null;
        if (type == TransactionType.DEPOSIT || type == TransactionType.WITHDRAW) {
            sql = "insert into deposit_withdraw_transaction values(?,?,?,?,?)";
        }
        if (type == TransactionType.TRANSFER) {
            sql = "insert into transfer_transaction values(?,?,?,?,?,?)";
        }
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transType);
            ps.setString(2, userId);
            ps.setObject(3, dateTime);
            ps.setBigDecimal(4, amount);
            if (type == TransactionType.DEPOSIT) {
                ps.setString(5, acNumTo);
            }
            if (type == TransactionType.WITHDRAW) {
                ps.setString(5, acNumFrom);

            }
            if (type == TransactionType.TRANSFER) {
                ps.setString(5, acNumFrom);
                ps.setString(6, acNumTo);
            }
            count = ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return count;
    }

    /**
     * The getTransactions method takes a userId to retrieve all
     * transactions of the user, and return the list of the
     * transactions.
     *
     * @param   userId
     * @return
     */
    public List<Transaction> getTransactions(String userId) {
        if (userId == null) {
            return null;
        }
        AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
        String ac1 = null;
        String ac2 = null;
        String ac3 = null;
        List<String> accountNumbers = accountDao.getAccountNumbers(userId);
        if (accountNumbers.size() == 1) {
            ac1 = accountNumbers.get(0);
        }
        if (accountNumbers.size() == 2) {
            ac1 = accountNumbers.get(0);
            ac2 = accountNumbers.get(1);
        }
        if (accountNumbers.size() == 3) {
            ac1 = accountNumbers.get(0);
            ac2 = accountNumbers.get(1);
            ac3 = accountNumbers.get(2);
        }
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = null;
        Account accountFrom = Account.NULL_ACCOUNT;
        Account accountTo = Account.NULL_ACCOUNT;
        String type;
        TransactionType transType;
        LocalDateTime dateTime;
        BigDecimal amount;
        String fromAcNum;
        String toAcNum;
        String sql = "select trans_type, userid, datetime, amount, ac_number, from_ac_num, to_ac_num from "
                + "(select trans_type, userid, datetime, amount, ac_number, null from_ac_num, null to_ac_num "
                + "from deposit_withdraw_transaction dwt union select trans_type, userid, datetime, amount, null, "
                + "from_ac_num, to_ac_num from transfer_transaction tt order by datetime desc) as foo "
                + "where userid=? or to_ac_num in (?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, ac1);
            ps.setString(3, ac2);
            ps.setString(4, ac3);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                type = rs.getString("trans_type");
                transType = type.equals("deposit") ? TransactionType.DEPOSIT : type.equals("withdraw") ?
                         TransactionType.WITHDRAW : TransactionType.TRANSFER;
                dateTime = rs.getObject("datetime", LocalDateTime.class);
                amount = rs.getBigDecimal("amount");
                if (transType == TransactionType.DEPOSIT) {
                    toAcNum = rs.getString("ac_number");
                    accountTo = accountDao.getAccount(toAcNum);
                    transaction = new Transaction(transType, userId, dateTime, amount, accountTo);
                }
                if (transType == TransactionType.WITHDRAW) {
                    fromAcNum = rs.getString("ac_number");
                    accountFrom = accountDao.getAccount(fromAcNum);
                    transaction = new Transaction(transType, userId, dateTime, accountFrom, amount);
                }
                if (transType == TransactionType.TRANSFER) {
                    fromAcNum = rs.getString("from_ac_num");
                    toAcNum = rs.getString("to_ac_num");
                    accountFrom = accountDao.getAccount(fromAcNum);
                    accountTo = accountDao.getAccount(toAcNum);
                    transaction = new Transaction(transType, userId, dateTime, accountFrom, amount, accountTo);
                }
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * The getTransactions method takes an accountNumber to retrieve all
     * transactions belongs to the account, and return the list of the
     * transactions.
     *
     * @param   accountNumber
     * @return
     */
    public List<Transaction> getAccountTransaction(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = null;
        Account accountFrom = Account.NULL_ACCOUNT;
        Account accountTo = Account.NULL_ACCOUNT;
        String type;
        TransactionType transType;
        LocalDateTime dateTime;
        BigDecimal amount;
        String fromAcNum;
        String toAcNum;
        String sql = "select trans_type, userid, datetime, amount, ac_number, from_ac_num, to_ac_num from " +
                "(select trans_type, userid, datetime, amount, ac_number, null from_ac_num, null to_ac_num " +
                "from deposit_withdraw_transaction dwt union select trans_type, userid, datetime, amount, null, " +
                "from_ac_num, to_ac_num from transfer_transaction tt order by datetime desc) as foo " +
                "where ac_number=? or to_ac_num=?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userId;
                type = rs.getString("trans_type");
                transType = type.equals("deposit") ? TransactionType.DEPOSIT : type.equals("withdraw") ?
                        TransactionType.WITHDRAW : TransactionType.TRANSFER;
                dateTime = rs.getObject("datetime", LocalDateTime.class);
                amount = rs.getBigDecimal("amount");
                if (transType == TransactionType.DEPOSIT) {
                    userId = rs.getString("userid");
                    toAcNum = rs.getString("ac_number");
                    accountTo = accountDao.getAccount(toAcNum);
                    transaction = new Transaction(transType, "", dateTime, amount, accountTo);
                }
                if (transType == TransactionType.WITHDRAW) {
                    userId = rs.getString("userid");
                    fromAcNum = rs.getString("ac_number");
                    accountFrom = accountDao.getAccount(fromAcNum);
                    transaction = new Transaction(transType, "", dateTime, accountFrom, amount);
                }
                if (transType == TransactionType.TRANSFER) {
                    userId = rs.getString("userid");
                    fromAcNum = rs.getString("from_ac_num");
                    toAcNum = rs.getString("to_ac_num");
                    accountFrom = accountDao.getAccount(fromAcNum);
                    accountTo = accountDao.getAccount(toAcNum);
                    transaction = new Transaction(transType, "", dateTime, accountFrom, amount, accountTo);
                }
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return transactions;
    }
}
