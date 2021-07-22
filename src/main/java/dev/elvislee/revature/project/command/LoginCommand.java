package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.User;
import dev.elvislee.revature.project.dao.UserDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;

/**
 * The LoginCommand class helps users to login into their
 * accounts.
 */
public class LoginCommand implements Command{
    private final UserDaoImpl userDao = UserDaoImpl.getUserDaoInstance();

    /**
     * The execute method prompt user for entering userId and password.
     * The information entered will be check with the database server record
     * for verification. The verified user will be displayed with a menu with
     * different operation choices.
     *
     * @return  1 for successful login, 0 for unsuccessful login
     */
    @Override
    public int execute() throws InterruptedOperationException {
        Logger logger = Log4j.getLogger();
        int success = 0;
        try {
            boolean validUserId = false;
            boolean validPassword = false;
            User user = null;
            while (!validUserId) {
                ConsoleHelper.printCancelInstruction();
                ConsoleHelper.printlnWithTab("[LOGIN]");
                ConsoleHelper.printWithTab("UserId : ");
                String userId = ConsoleHelper.readString();
                user = userDao.getUser(userId);
                if (user != User.NULL_USER && user.getUserId().equalsIgnoreCase(userId)) {
                    validUserId = true;
                } else {
                    ConsoleHelper.printlnWithTab("The UserId is not correct, please try again.");
                }
            }
            while (validUserId && !validPassword) {
                ConsoleHelper.printWithTab("Password: ");
                String password = ConsoleHelper.readString();
                if (user.getPassword().equals(password)) {
                    validPassword = true;
                } else {
                    ConsoleHelper.printlnWithTab("The password is not correct, please try again.");
                }
            }

            LocalDateTime now = LocalDateTime.now();
            success = userDao.updateUserLoginDateTime(user, now);
            if (success == 1) {
                BankApp.setCurrentUser(user);
                ConsoleHelper.printlnWithTab("Login successful, welcome!");
            } else {
                ConsoleHelper.printlnWithTab("System failure, please login later.");
            }
            ConsoleHelper.println("");
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
