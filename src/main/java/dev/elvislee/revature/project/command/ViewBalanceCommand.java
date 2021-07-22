package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.AccountType;
import dev.elvislee.revature.project.model.User;
import dev.elvislee.revature.project.dao.AccountDaoImpl;
import dev.elvislee.revature.project.dao.UserDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;

import java.math.BigDecimal;
import java.util.List;

/**
 * The ViewBalanceCommand class helps to display the balance records of all
 * accounts of the user.
 */
public class ViewBalanceCommand implements Command {
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
    private final UserDaoImpl userDao = UserDaoImpl.getUserDaoInstance();

    /**
     * The execute method displays the Account Number, Account Type and Balance of each
     * of the accounts of user.
     *
     * @return  1 for successful login, 0 for unsuccessful login
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        int success = 0;
        try {
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.println("[VIEW BALANCE]");
            ConsoleHelper.printPartitionline();
            ConsoleHelper.println(String.format("%-20.20s%-50.50s%20.20s", "Account Number", "Account Type", "Balance (USD)"));
            ConsoleHelper.printPartitionline();

            int count = 0;
            List<String> accountNumbers = accountDao.getAccountNumbers(BankApp.getCurrentUser().getUserId());
            for (String accountNumber : accountNumbers) {
                Account account = accountDao.getAccount(accountNumber);
                BigDecimal balance = account.getBalance();
                String acNum = accountNumber.substring(0, 3) + "-" + accountNumber.substring(3, 6) + "-" + accountNumber.substring(6);
                AccountType acType = account.getAccountType();
                String acName = acType == AccountType.SAVING ? "Saving Account" : acType == AccountType.CHECKING ?
                        "Checking Account" : "Joint Account";
                if (acType == AccountType.JOINT) {
                    for (String userId : account.getUserIds()) {
                        if (!userId.equals(BankApp.getCurrentUser().getUserId())) {
                            User secondUser = userDao.getUser(userId);
                            acName += " with " + secondUser.getFirstName() + " " + secondUser.getLastName();
                        }
                    }
                }
                ConsoleHelper.println(String.format("%-20.20s%-50.50s%,20.2f", acNum, acName, balance));
                count++;
            }
            success = count == accountNumbers.size() ? 1 : 0;
            ConsoleHelper.printPartitionline();
            ConsoleHelper.print("Press ENTER to continue.");
            ConsoleHelper.readString();
        } catch (CancelOperationException e) {
            // do nothing, allow user to return to main menu
        }
        return success;
    }
}
