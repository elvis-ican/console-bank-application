package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.dao.AccountDao;
import dev.elvislee.revature.project.dao.AccountDaoImpl;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.Transaction;
import dev.elvislee.revature.project.model.TransactionType;
import dev.elvislee.revature.project.dao.TransactionDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The ViewTransactionCommand class helps to display all transaction records of all
 * accounts of the user.
 */
public class ViewTransactionsCommand implements Command {
    private final TransactionDaoImpl transactionDao = TransactionDaoImpl.getTransactionDAOInstance();
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();

    /**
     * The execute method displays the transaction date time, transaction type, account(s) involved
     * and the amount of each of the transaction involving the accounts of the user.
     *
     * @return  1 for successful login, 0 for unsuccessful login
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        int success = 0;
        try {
            String accountNumber = getViewTransactionChoice();
            List<Transaction> transactions;
            if (accountNumber.equals("all")) {
                transactions = transactionDao.getTransactions(BankApp.getCurrentUser().getUserId());
            } else {
                transactions = transactionDao.getAccountTransaction(accountNumber);
            }
            String date = "";
            String message = "";
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.println("[[TRANSACTION HISTORY]]");
            ConsoleHelper.printPartitionline();
            ConsoleHelper.println(String.format("%-20.20s%-50.50s%20.20s", "Date", "Transaction", "Amount(USD)"));
            ConsoleHelper.printPartitionline();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int count = 0;
            for (Transaction transaction : transactions) {
                date = transaction.getDateTime().format(formatter);
                BigDecimal amount = transaction.getAmount();
                String fromAc = transaction.getFrom().getAccountNumber();
                String toAc = transaction.getTo().getAccountNumber();
                String from = "";
                String to = "";
                if (fromAc != null) {
                    from = fromAc.substring(0, 3) + "-" + fromAc.substring(3, 6) + "-" + fromAc.substring(6);
                }
                if (toAc != null) {
                    to = toAc.substring(0, 3) + "-" + toAc.substring(3, 6) + "-" + toAc.substring(6);
                }
                if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
                    message = "Deposit into " + to;
                    ConsoleHelper.printTransRecord(date, message, amount);
                    count++;
                }
                if (transaction.getTransactionType() == TransactionType.WITHDRAW) {
                    message = "Withdraw from " + from;
                    ConsoleHelper.printTransRecord(date, message, amount);
                    count++;
                }
                if (transaction.getTransactionType() == TransactionType.TRANSFER) {
                    message = "Transfer from " + from + " to " + to;
                    ConsoleHelper.printTransRecord(date, message, amount);
                    count++;
                }
            }
            success = count == transactions.size() ? 1 : 0;
            ConsoleHelper.printPartitionline();
            ConsoleHelper.printPressEnterToContinue();
        } catch (CancelOperationException e) {

        }
        return success;
    }

    /**
     * The getViewTransactionChoice method displays the account list and prompt user to choose to view
     * transaction history of one account or all accounts.
     */
    public String getViewTransactionChoice() throws CancelOperationException, InterruptedOperationException {
        String accountNumber = null;

            String userId = BankApp.getCurrentUser().getUserId();
            List<String> accountNumbers = accountDao.getAccountNumbers(userId);
            ConsoleHelper.cleanScreen();
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.printlnWithTab("[VIEW TRANSACTIONS HISTORY]");

            ConsoleHelper.printAccountList();
            ConsoleHelper.printlnWithTab(String.format("%d -  %-20.20s%-60.60s", accountNumbers.size() + 1, "All Accounts", ""));
            boolean isValidInput = false;
            do {
                ConsoleHelper.printWithTab("Account: ");
                String input = ConsoleHelper.readString();
                for (int i = 1; i <= accountNumbers.size(); i++) {
                    if (input.equals(String.valueOf(i))) {
                        accountNumber = accountNumbers.get(i - 1);
                        isValidInput = true;
                        break;
                    }
                }
                if (input.equals(String.valueOf(accountNumbers.size() + 1))) {
                    accountNumber = "all";
                    isValidInput = true;
                }
            } while (!isValidInput);

        return accountNumber;
    }
}
