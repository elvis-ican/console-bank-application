package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.Transaction;
import dev.elvislee.revature.project.model.TransactionType;
import dev.elvislee.revature.project.dao.AccountDaoImpl;
import dev.elvislee.revature.project.dao.TransactionDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The TransferCommand class helps users to transfer an amount between their
 * own accounts, or to accounts of other users.
 */
public class TransferCommand implements Command{
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
    private final TransactionDaoImpl transactionDao = TransactionDaoImpl.getTransactionDAOInstance();

    /**
     * The TransferCommand execute method will prompt users for choosing one of their own accounts
     * to withdraw from and one of their other account or third person account to deposit into.
     * For account number not belongs to the user, the user needs to input the account number of
     * the other account. The amount to transfer will be checked to meet the criterion that it should
     * be greater than zero and with at most two decimal places. After verified, the method will invoke
     * the AccountDao transfer method to update balance in the database.
     *
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        Logger logger = Log4j.getLogger();
        int success = 0;
        try {
            String fromAccountNumber = null;
            String toAccountNumber = null;
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal originalBalance = BigDecimal.ZERO;
            Transaction transaction;
            LocalDateTime now;
            List<String> accountNumbers = accountDao.getAccountNumbers(BankApp.getCurrentUser().getUserId());
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.printlnWithTab("[TRANSFER]");
            ConsoleHelper.printAccountList();

            boolean isValidInput = false;
            do {
                ConsoleHelper.printWithTab("From: ");
                String input = ConsoleHelper.readString();
                for (int i = 1; i <= accountNumbers.size(); i++) {
                    if (input.equals(String.valueOf(i))) {
                        fromAccountNumber = accountNumbers.get(i - 1);
                        isValidInput = true;
                        break;
                    }
                }
            } while (!isValidInput);

            ConsoleHelper.printAccountList();
            ConsoleHelper.printlnWithTab(String.format("%d -  %-20.20s%-60.60s", accountNumbers.size() + 1, "Other Account", ""));
            isValidInput = false;
            do {
                ConsoleHelper.printWithTab("To: ");
                String input = ConsoleHelper.readString();
                for (int i = 1; i <= accountNumbers.size(); i++) {
                    if (input.equals(String.valueOf(i))) {
                        toAccountNumber = accountNumbers.get(i - 1);
                        isValidInput = true;
                        break;
                    }
                }
                if (input.equals(String.valueOf(accountNumbers.size() + 1))) {
                    ConsoleHelper.printWithTab("Account number: ");
                    toAccountNumber = ConsoleHelper.readString();
                    if (accountDao.getAccount(toAccountNumber) != Account.NULL_ACCOUNT) {
                        isValidInput = true;
                        break;
                    } else {
                        ConsoleHelper.printlnWithTab("No such account!");
                        ConsoleHelper.println("");
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            logger.error("Interrupted Exception when thread sleep", e);
                        }
                        break;
                    }
                }
            } while (!isValidInput);

            originalBalance = accountDao.getAccount(fromAccountNumber).getBalance();

            if (isValidInput) {
                boolean isValidAmount = false;
                while (!isValidAmount) {
                    ConsoleHelper.printWithTab("Amount: ");
                    String input = ConsoleHelper.readString();
                    try {
                        double inputValue = Double.parseDouble(input);
                        amount = BigDecimal.valueOf(inputValue);
                        int decimalPlaces = ConsoleHelper.getNumberOfDecimalPlaces(amount);
                        if (decimalPlaces < 3) {
                            isValidAmount = true;
                        } else {
                            ConsoleHelper.printlnWithTab("Invalid amount, we only accept transfer amount up to two decimal places.");
                        }
                    } catch (NumberFormatException e) {
                        // the amount format is not valid
                    }
                }
                if (amount.compareTo(originalBalance) > 0) {
                    ConsoleHelper.printlnWithTab("Insufficient fund, transfer was cancelled!");
                } else {
                    int countTransfer = accountDao.transfer(fromAccountNumber, toAccountNumber, amount);
                    now = LocalDateTime.now();
                    Account from = accountDao.getAccount(fromAccountNumber);
                    Account to = accountDao.getAccount(toAccountNumber);
                    transaction = new Transaction(TransactionType.TRANSFER, BankApp.getCurrentUser().getUserId(), now, from, amount, to);
                    int countTransaction = transactionDao.addTransaction(transaction);
                    if (countTransaction == 1 && countTransfer == 1) {
                        success = 1;
                        ConsoleHelper.printlnWithTab("Transfer completed.");
                    }
                }

                ConsoleHelper.println("");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    logger.error("Interrupted Exception when thread sleep", e);
                }
            }
        } catch (CancelOperationException e) {
            // do nothing, allow user to return to main menu
        }
        return success;
    }
}
