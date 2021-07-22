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
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The CreateAccountCommand class helps to create new account
 * for user.
 * <p>
 * Each user can create one Saving Account, one Checking Account,
 * and no limit number but unique Joint Account of two persons.
 */
public class CreateAccountCommand implements Command{
    private final AccountDaoImpl accountDao = AccountDaoImpl.getAccountDaoInstance();
    private final UserDaoImpl userDao = UserDaoImpl.getUserDaoInstance();
    private static Logger logger = Log4j.getLogger();

    /**
     * The execute method will prompt user for inputting choices of account
     * and verify user's information for creating account.
     * For creating Joint Account, the userId and password of the other user
     * must be correctly inputted, which assumes that the other person is
     * present and authorize the creation of the account.
     *
     * @return   1 for successful account creation and 0 for unsuccessful
     *
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        int success = 0;
        try {
            Account account;
            String currentUserId = BankApp.getCurrentUser().getUserId();
            ConsoleHelper.println("");
            ConsoleHelper.displayMenuBanner();
            ConsoleHelper.printMenuBar("CREATE ACCOUNT");
            String input = ConsoleHelper.requestAccountType();

            AccountType accountType = AccountType.getAccountType(Integer.parseInt(input));
            User user = BankApp.getCurrentUser();
            if (accountType == AccountType.JOINT) {
                ConsoleHelper.println("");
                createJointAccount(user, success);
            } else if (accountDao.getAccount(user.getUserId(), accountType) != Account.NULL_ACCOUNT) {
                ConsoleHelper.printlnWithTab("The account already exists.");
                ConsoleHelper.printPressEnterToContinue();
            } else {
                List<String> userIds = new ArrayList<>();
                userIds.add(currentUserId);
                if (accountType == AccountType.SAVING) {
                    account = new Account(userIds, AccountType.SAVING);
                    success = accountDao.addAccount(currentUserId, account);
                    if (success == 1) {
                        ConsoleHelper.printlnWithTab("The Saving Account is created.");
                    } else {
                        ConsoleHelper.printlnWithTab("System failure, can't create account now. Please try later");
                    }
                    ConsoleHelper.printPressEnterToContinue();
                }
                if (accountType == AccountType.CHECKING) {
                    account = new Account(userIds, AccountType.CHECKING);
                    success = accountDao.addAccount(currentUserId, account);
                    if (success == 1) {
                        ConsoleHelper.printlnWithTab("The Checking Account is created.");
                    } else {
                        ConsoleHelper.printlnWithTab("System failure, can't create account now. Please try later");
                    }
                    ConsoleHelper.printPressEnterToContinue();
                }
            }
        } catch (CancelOperationException e) {
            // do nothing, allow user to return to main menu
        }
        return success;
    }

    /**
     * The createJointAccount method will help to check the second user information
     * to validate whether the Joint Account can be created.
     * The rule is Joint Account users must be registered, each Joint Account can only
     * has two users, the same combination of two users can only has one Joint Account.
     *
     * @param   user
     * @param   success
     *
     */
    private void createJointAccount(User user, int success) throws InterruptedOperationException, CancelOperationException {
        Account account;
        String currentUserId = BankApp.getCurrentUser().getUserId();
        boolean cancel = false;
        String secondUserId = null;
        User secondUser = null;
        String secondUserPassword = null;
        ConsoleHelper.printSubMenuBar("CREATE JOINT ACCOUNT");
        boolean isValidSecondUser = false;
        while (!cancel && !isValidSecondUser) {
            ConsoleHelper.printWithTab("Second User's UserId: ");
            secondUserId = ConsoleHelper.readString();
            cancel = secondUserId.equalsIgnoreCase("c");
            if (!cancel) {
                secondUser = userDao.getUser(secondUserId);
                if (secondUser != User.NULL_USER) {
                    isValidSecondUser = true;
                } else {
                    ConsoleHelper.printlnWithTab("The UserId you entered doesn't exist. Try again.");
                    ConsoleHelper.println("");
                }
            }
        }

        if (!cancel) {
            for (String accountNumber : accountDao.getAccountNumbers(user.getUserId())) {
                Account tempAccount = accountDao.getAccount(accountNumber);
                if (tempAccount.getAccountType() == AccountType.JOINT) {
                    if (tempAccount.getUserIds().contains(secondUserId)) {
                        ConsoleHelper.printlnWithTab("The Joint Account already exists.");
                        ConsoleHelper.printPressEnterToContinue();
                        break;
                    }
                }
            }
        }

        boolean isValidPassword = false;
        while (!cancel && isValidSecondUser && !isValidPassword) {
            ConsoleHelper.printWithTab("Second User's Password: ");
            secondUserPassword = ConsoleHelper.readString();
            cancel = secondUserPassword.equalsIgnoreCase("c");
            if (!secondUserPassword.equals(secondUser.getPassword())) {
                ConsoleHelper.printlnWithTab("The Password is not correct. Try again.");
                ConsoleHelper.println("");
            } else {
                List<String> userIds = new ArrayList<>();
                userIds.add(currentUserId);
                userIds.add(secondUserId);
                account = new Account(userIds, AccountType.JOINT);
                success = accountDao.addAccount(currentUserId, secondUserId, account);
                isValidPassword = true;
                if (success == 1) {
                    ConsoleHelper.printlnWithTab("The Joint Account is created.");
                } else {
                    ConsoleHelper.printlnWithTab("System failure, can't create account now. Please try later");
                }
                ConsoleHelper.printPressEnterToContinue();
            }
        }
    }
}
