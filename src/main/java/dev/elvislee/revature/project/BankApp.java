package dev.elvislee.revature.project;

import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.*;
import dev.elvislee.revature.project.command.CommandExecutor;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;

/**
 * The Bank Application driving method to run the program.
 * <p>
 * This method shows the entry page for user to choose login
 * or register. The method will loop to show the menu for
 * user choosing various functions until the user log out of
 * the program
 *
 * @param  args  no special effect for this application
 *
 */

public class BankApp {
    private static boolean confirmLogout = false;
    private static User currentUser = User.NULL_USER;
    public static void setConfirmLogout(boolean confirmLogout) {
        BankApp.confirmLogout = confirmLogout;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User currentUser) {
        BankApp.currentUser = currentUser;
    }

    public static void main(String[] args) {
        Operation operation = null;
        try {
            do {
                // display the login page
                do {
                    ConsoleHelper.println("");
                    ConsoleHelper.displayLoginBanner();
                    operation = ConsoleHelper.requestLoginOperation();
                    CommandExecutor.execute(operation);
                } while (BankApp.currentUser == User.NULL_USER);

                // display the menu
                do {
                    ConsoleHelper.displayMenuBanner();
                    operation = ConsoleHelper.requestOperation();
                    CommandExecutor.execute(operation);
                } while (!confirmLogout);
            } while (true);
        } catch (InterruptedOperationException | CancelOperationException e) {
            ConsoleHelper.printExitMessage();
        }
    }
}
