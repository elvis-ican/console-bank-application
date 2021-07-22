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
 * The WithdrawCommand class helps users to deposit into their
 * accounts.
 * User can choose which account to deposit into. Only whole number
 * amount will be allowed.
 */
public class WithdrawCommand implements Command {
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
    private final TransactionDaoImpl transactionDao = TransactionDaoImpl.getTransactionDAOInstance();

    /**
     * The execute method will prompt user for choosing the account to withdraw from,
     * as well as inputting the amount to withdraw. The method will check the amount
     * to make sure only positive whole number is accepted, and there is sufficient
     * fund in the account. After verified, the method will invoke the AccountDao
     * withdraw method to update balance in the database.
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
            BigDecimal originalBalance = BigDecimal.ONE;
            LocalDateTime now = LocalDateTime.now();
            Transaction transaction;
            List<String> accountNumbers = accountDao.getAccountNumbers(BankApp.getCurrentUser().getUserId());
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.printlnWithTab("[WITHDRAW]");
            ConsoleHelper.printAccountList();

            boolean isValidInput = false;
            do {
                ConsoleHelper.printWithTab("From: ");
                String input = ConsoleHelper.readString();
                for (int i = 1; i <= accountNumbers.size(); i++) {
                    if (input.equals(String.valueOf(i))) {
                        accountNumber = accountNumbers.get(i - 1);
                        isValidInput = true;
                        break;
                    }
                }
            } while (!isValidInput);

            Account account = accountDao.getAccount(accountNumber);
            originalBalance = account.getBalance();

            isValidInput = false;
            do {
                ConsoleHelper.printWithTab("Amount: ");
                String input = ConsoleHelper.readString();
                try {
                    double inputValue = Double.parseDouble(input);
                    amount = BigDecimal.valueOf(inputValue);
                    int decimalPlaces = ConsoleHelper.getNumberOfDecimalPlaces(amount);
                    if (decimalPlaces == 0) {
                        isValidInput = true;
                    } else {
                        ConsoleHelper.printlnWithTab("Only whole number withdraw is accepted.");
                    }
                } catch (NumberFormatException e) {
                    // the amount format is not valid
                }
            } while (!isValidInput);
            if (amount.compareTo(originalBalance) > 0) {
                ConsoleHelper.printlnWithTab("Insufficient fund, withdraw cancelled!");
            } else {
                int countAccount = accountDao.withdraw(accountNumber, amount);
                now = LocalDateTime.now();
                transaction = new Transaction(TransactionType.WITHDRAW, BankApp.getCurrentUser().getUserId(), now, account, amount);
                int countTransaction = transactionDao.addTransaction(transaction);
                success = countAccount == 1 && countTransaction == 1 ? 1 : 0;
                ConsoleHelper.printlnWithTab("Withdraw completed.");
            }
            ConsoleHelper.println("");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                logger.error("Interrupted Exception when thread sleep", e);
            }
        } catch (CancelOperationException e) {

        }
        return success;
    }
}
