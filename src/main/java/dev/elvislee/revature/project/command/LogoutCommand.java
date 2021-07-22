package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.User;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

/**
 * The LogoutCommand class helps users to logout the application.
 */
public class LogoutCommand implements Command {

    /**
     * The execute method prompt user to confirm logout. The method will set the current
     * user method to NULL_USER, and return to displaying the login/register page.
     *
     *  @return  1 for successful login, 0 for unsuccessful login
     */
    @Override
    public int execute() throws InterruptedOperationException, CancelOperationException {
        Logger logger = Log4j.getLogger();
        int success = 0;
        String input;
        ConsoleHelper.println("");
        do {
            ConsoleHelper.printWithTab("Are you sure to log out? y/n: ");
            input = ConsoleHelper.readString();
        } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n"));
        if (input.equals("y")) {
            BankApp.setCurrentUser(User.NULL_USER);
            ConsoleHelper.printlnWithTab("Thank you for using the Bank Application System.");
            ConsoleHelper.println("");
            BankApp.setConfirmLogout(true);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                logger.error("Interrupted Exception when thread sleep", e);
            }
        }
        return success;
    }
}
