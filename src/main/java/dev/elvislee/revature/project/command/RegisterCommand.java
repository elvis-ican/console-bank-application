package dev.elvislee.revature.project.command;


import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.User;
import dev.elvislee.revature.project.dao.UserDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

/**
 * The RegisterCommand class helps new user to register in the BankApp.
 */
public class RegisterCommand implements Command{
    private final UserDaoImpl userDao = UserDaoImpl.getUserDaoInstance();

    /**
     * The RegisterCommand execute method will prompt user for choosing a userId
     * with at least 5 characters, the method will check with the database record
     * to make sure it is unique as the primary key for the user. The password inputted
     * will also be checked to meet 8 characters length requirement. With all information
     * correctly inputted, the user is registered as the user of the BankApp.
     *
     * @return  1 for successful login, 0 for unsuccessful login
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        Logger logger = Log4j.getLogger();
        int success = 0;
        try {
            String userId = null;
            String password = null;
            String firstName = null;
            String lastName = null;
            boolean validInput = false;

            ConsoleHelper.printCancelInstruction();
            ConsoleHelper.printlnWithTab("[Register]");
            ConsoleHelper.printlnWithTab("Enter a UserId with at least 5 characters and is case-sensitive");
             do {
                ConsoleHelper.printWithTab("UserId: ");
                userId = ConsoleHelper.readString();
                if (userDao.getUser(userId) != User.NULL_USER) {
                    ConsoleHelper.printlnWithTab("The UserId is not available, try another one.");
                } else if (userId.length() < 5) {
                    ConsoleHelper.printlnWithTab("UserId should have at least 5 characters.");
                } else {
                    validInput = true;
                }
            } while (!validInput);


            do {
                validInput = false;
                ConsoleHelper.printlnWithTab("Enter a password with at least 8 characters and is case-sensitive.");
                ConsoleHelper.printWithTab("Password: ");
                password = ConsoleHelper.readString();

                    if (password.length() < 8) {
                        ConsoleHelper.printlnWithTab("The password must have at least 8 characters.");
                    } else {
                        validInput = true;
                    }
            } while (!validInput);

            do {
                validInput = false;
                ConsoleHelper.printWithTab("First Name: ");
                firstName = ConsoleHelper.readString();
                if (firstName == null || firstName.replaceAll(" ", "").length() == 0) {
                    ConsoleHelper.printlnWithTab("First name should not be empty.");
                } else {
                    validInput = true;
                }
            } while (!validInput);

            do {
                validInput = false;
                ConsoleHelper.printWithTab("Last Name: ");
                lastName = ConsoleHelper.readString();
                if (lastName == null || lastName.replaceAll(" ", "").length() == 0) {
                    ConsoleHelper.printlnWithTab("Last name should not be empty.");
                } else {
                    validInput = true;
                }
            } while (!validInput);

            if (validInput) {
                User user = new User(userId, firstName, lastName, password);
                success = userDao.addUser(user) ? 1 : 0;
                if (success == 1) {
                    ConsoleHelper.printlnWithTab("Welcome, you are registered. Please login to create accounts.");
                    ConsoleHelper.println("");
                }
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
