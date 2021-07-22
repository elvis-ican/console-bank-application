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
 * The DepositCommand class helps users to deposit into their
 * accounts.
 * User can choose which account to deposit into. Only whole number
 * amount will be allowed.
 */

public class DepositCommand implements Command {
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
    private final TransactionDaoImpl transactionDao = TransactionDaoImpl.getTransactionDAOInstance();

    /**
     * The execute method will prompt user for choosing the account to deposit into,
     * as well as inputting the amount of deposit. The method will check the amount
     * to make sure only positive whole number is accepted. After verified, the method will invoke
     * the AccountDao deposit method to update balance in the database.
     *
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        Logger logger = Log4j.getLogger();
        int success = 0;
        try {
            String accountNumber = null;
            BigDecimal amount = BigDecimal.ONE;
            Transaction transaction;
            LocalDateTime now;
            String userId = BankApp.getCurrentUser().getUserId();
            List<String> accountNumbers = accountDao.getAccountNumbers(userId);
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.printlnWithTab("[DEPOSIT]");
            ConsoleHelper.printAccountList();

            boolean isValidInput = false;
            do {
                ConsoleHelper.printWithTab("Into: ");
                String input = ConsoleHelper.readString();
                for (int i = 1; i <= accountNumbers.size(); i++) {
                    if (input.equals(String.valueOf(i))) {
                        accountNumber = accountNumbers.get(i - 1);
                        isValidInput = true;
                        break;
                    }
                }

            } while (!isValidInput);

            isValidInput = false;
            do {
                ConsoleHelper.printWithTab("Amount: ");
                String input = ConsoleHelper.readString();
                try {
                    double inputValue = Double.parseDouble(input);
                    amount = BigDecimal.valueOf(inputValue);
                    int decimalPlaces = ConsoleHelper.getNumberOfDecimalPlaces(amount);
                    if (decimalPlaces == 0 && amount.compareTo(BigDecimal.valueOf(0)) > 0) {
                        isValidInput = true;
                    } else {
                        ConsoleHelper.printlnWithTab("Invalid amount, we only accept whole number deposit.");
                    }
                } catch (NumberFormatException e) {
                    // the amount format is not valid
                }
            } while (!isValidInput);

            int countAccount = accountDao.deposit(accountNumber, amount);
            now = LocalDateTime.now();
            Account account = accountDao.getAccount(accountNumber);
            transaction = new Transaction(TransactionType.DEPOSIT, userId, now, amount, account);
            int countTransaction = transactionDao.addTransaction(transaction);
            success = countAccount == 1 && countTransaction == 1 ? 1 : 0;
            if (success == 1) {
                ConsoleHelper.printlnWithTab("Deposit completed.");
                ConsoleHelper.println("");
            } else {
                ConsoleHelper.printlnWithTab("System failure, deposit cancelled. Please try later.");
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                logger.error("Interrupted Exception when thread sleep", e);
            }
        } catch (CancelOperationException e) {
            // do nothing, allow user to return to main menu
        }
        return success;
    }
}
