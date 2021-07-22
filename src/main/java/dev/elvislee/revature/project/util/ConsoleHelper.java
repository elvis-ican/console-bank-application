package dev.elvislee.revature.project.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import dev.elvislee.revature.project.BankApp;
import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.model.Account;
import dev.elvislee.revature.project.model.AccountType;
import dev.elvislee.revature.project.dao.AccountDaoImpl;
import dev.elvislee.revature.project.dao.UserDaoImpl;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.model.Operation;

/**
 * The ConnectionHelper class provides various static methods to handle the reading
 * inputs from user and dsplay information to the user.
 */
public class ConsoleHelper {
    private static final BufferedReader bis = new BufferedReader(new InputStreamReader(System.in));
    private static final AccountDaoImpl ACCOUNT_DAO = AccountDaoImpl.getAccountDaoInstance();
    private static final UserDaoImpl USER_DAO = UserDaoImpl.getUserDaoInstance();
    private static String tab = "          ";
    public static void print(String message) { System.out.print(message); }
    public static void println(String message) { System.out.println(message); }
    public static void printWithTab(String message) {
        System.out.print(tab + message);
    }
    public static void printlnWithTab(String message) {
        System.out.println(tab + message);
    }

    /**
     * The readString method read input from user. If the user inputs "c", it will throw
     * a new CancelOperationException which allows to user to cancel the operation anywhere to
     * return to the menu display. While a secret key "__confirmExit__" is served for terminating
     * the application anywhere by the system administrator of the application.
     */
    public static String readString() throws InterruptedOperationException, CancelOperationException {
        String input = null;
        try {
            input = bis.readLine();
            if (input.equals("__confirmExit__")) {
                throw new InterruptedOperationException();
            }
            if (input.equalsIgnoreCase("c")) {
                throw new CancelOperationException();
            }
        } catch (IOException e) {

        }
        return input;
    }

    /**
     * The getNumberOfDecimalPlaces method takes a BigDecimal value and return the number
     * of decimal places of it aim at verify whether the user input money amount is
     * an appropriate one.
     *
     * @param   bigDecimal
     * @return
     */
    public static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    /**
     * The requestOperation method displays the menu of the application to prompt and listen for the user to
     * input operation code, after verified will return the enum Operation Code for invoking the
     * corresponding operation method.
     *
     * @return
     */
    public static Operation requestOperation() throws InterruptedOperationException, CancelOperationException {
        boolean correctInput = false;
        String s = "";
        while (!correctInput) {
            ConsoleHelper.printlnWithTab("[MENU]");
            ConsoleHelper.printlnWithTab("1 - CREATE ACCOUNT");
            ConsoleHelper.printlnWithTab("2 - VIEW BALANCE");
            ConsoleHelper.printlnWithTab("3 - VIEW TRANSACTIONS HISTORY");
            ConsoleHelper.printlnWithTab("4 - DEPOSIT");
            ConsoleHelper.printlnWithTab("5 - WITHDRAW");
            ConsoleHelper.printlnWithTab("6 - TRANSFER");
            ConsoleHelper.printlnWithTab("7 - LOGOUT");
            ConsoleHelper.printWithTab("Choose operation: ");
            s = ConsoleHelper.readString();
            if (s.length() == 1 && s.charAt(0) >= '1' && s.charAt(0) <= '7' ) {
                correctInput = true;
            }
        }
        return Operation.getOperation(Integer.parseInt(s));
    }

    /**
     * The requestLogin method displays the front page of the application to prompt and listen for the user
     * to choose login or register, after verified the inputting code, it will return the enum Operation Code
     * for invoking the corresponding operation method.
     *
     * @return
     */
    public static Operation requestLoginOperation() throws InterruptedOperationException, CancelOperationException {
        boolean correctInput = false;
        String s = "";
        Operation operation = null;
        while (!correctInput) {
            ConsoleHelper.printlnWithTab("1 - LOGIN");
            ConsoleHelper.printlnWithTab("2 - REGISTER");
            ConsoleHelper.printWithTab("Choose operation: ");
            s = ConsoleHelper.readString();
            if (s.equals("1")) {
                operation = Operation.LOGIN;
                correctInput = true;
            }
            if (s.equals("2")) {
                operation = Operation.REGISTER;
                correctInput = true;
            }
        }
        return operation;
    }

    /**
     * The requestAccountType method displays the menu of the application to prompt and listen for the user to
     * choose which account to work with, after verified it will return a String representing the account
     * of choosing.
     *
     * @return
     */
    public static String requestAccountType() throws InterruptedOperationException, CancelOperationException {
        boolean correctType = false;
        String s = "";
        while (!correctType) {
            ConsoleHelper.printlnWithTab("1 - SAVING ACCOUNT");
            ConsoleHelper.printlnWithTab("2 - CHECK ACCOUNT");
            ConsoleHelper.printlnWithTab("3 - JOINT ACCOUNT");
            ConsoleHelper.printWithTab("Choose Account Type: ");
            s = ConsoleHelper.readString();
            if (s.equals("1") || s.equals("2") || s.equals("3")) {
                correctType = true;
            }
        }
        return s;
    }

    /**
     * The printAccountList method displays all the account details of the current user.
     *
     * @return
     */
    public static void printAccountList() {
        List<String> accountNumbers = ACCOUNT_DAO.getAccountNumbers(BankApp.getCurrentUser().getUserId());
        int n = accountNumbers.size();
        for (int i = 0; i < n; i++) {
            Account account = ACCOUNT_DAO.getAccount(accountNumbers.get(i));
            String acNum = accountNumbers.get(i).substring(0, 3) + "-" + accountNumbers.get(i).substring(3, 6) +
                    "-" + accountNumbers.get(i).substring(6);
            AccountType acType = account.getAccountType();
            String acName = acType == AccountType.SAVING ? "Saving Account" : acType == AccountType.CHECKING ?
                    "Checking Account" : "Joint Account";
            if (acType == AccountType.JOINT) {
                for (String userId : account.getUserIds()) {
                    if (!userId.equals(BankApp.getCurrentUser().getUserId())) {
                        acName += " with " + USER_DAO.getUser(userId).getFirstName() + " " + USER_DAO.getUser(userId).getLastName();
                    }
                }
            }
            ConsoleHelper.printlnWithTab(String.format("%d -  %-20.20s%-60.60s", i + 1, acNum, acName));
        }
    }

    /**
     * The displayLoginBanner displays the banner for the login page.
     */
    public static void displayLoginBanner() {
        ConsoleHelper.printBanner();
        ConsoleHelper.printStarLine();
        ConsoleHelper.println("");
    }

    /**
     * The displayMenuBanner displays the banner for the Menu page with user information and cancel.
     */
    public static void displayMenuBanner() {
        ConsoleHelper.printBanner();
        ConsoleHelper.printUserInfo();
        ConsoleHelper.printStarLine();
        ConsoleHelper.println("");
        //ConsoleHelper.printCancelInstruction();
    }

    /**
     * The printUserInfo method displays a line with user first and last name, as well as the last login date time.
     */
    public static void printUserInfo() {
        String name = BankApp.getCurrentUser().getFirstName() + " " + BankApp.getCurrentUser().getLastName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime lastLoginDateTime = BankApp.getCurrentUser().getLastLoginDateTime();
        String lastLogin;
        if (lastLoginDateTime != null) {
            lastLogin = "Last login: " + lastLoginDateTime.format(formatter);
        } else lastLogin = "Last login: --";
        ConsoleHelper.println(String.format("%-40.40s%52.52s", name, lastLogin));
    }

    /**
     * The printCancelInstruction method displays a line to instruct user to enter "c"
     * if want to cancel the operation, which will bring user to the menu page.
     */
    public static void printCancelInstruction() {
        ConsoleHelper.println(String.format("%92s", "> enter \"C\" to cancel"));
    }

    /**
     * The printPressEnterToContinue method displays a line to instruct user to press ENTER
     * to continue the next step.
     */
    public static void printPressEnterToContinue() throws InterruptedOperationException, CancelOperationException {
        ConsoleHelper.println("");
        ConsoleHelper.printWithTab("Press ENTER to continue.");
        ConsoleHelper.readString();
        ConsoleHelper.println("");
    }

    /**
     * The printMenuBar method displays the menu title of the specific operation in process.
     *
     * @param title
     */
    public static void printMenuBar(String title) {
        String cancel = "> press \"C\" to cancel";
        String menuBar = String.format("%-30.30s%52s", "[" + title + "]", cancel);
        ConsoleHelper.printlnWithTab(menuBar);
    }

    /**
     * The printSubMenuBar method displays the sub-menu title of the specific operation in process.
     *
     * @param title
     */
    public static void printSubMenuBar(String title) {
        String menuBar = String.format("%-30.30s", "[[" + title + "]]");
        ConsoleHelper.printlnWithTab(menuBar);
    }

    /**
     * The printTransRecord method takes the transaction details and display the transaction record.
     *
     * @param date
     * @param message
     * @param amount
     */
    public static void printTransRecord(String date, String message, BigDecimal amount) {
        ConsoleHelper.println(String.format("%-20.20s%-50.50s%,20.2f", date, message, amount));
    }

   /**
    * The printExit method display the application terminated message to the user.
    */
    public static void printExitMessage() {
        ConsoleHelper.printlnWithTab("Session end. Thank you for using the BANK Application System!");
    }

    /**
     * The printBanner method display the BankApp banner.
     */
    public static void printBanner() {
        printStarLine();
        ConsoleHelper.println("");
        ConsoleHelper.println("                                  Bank Application System");
        ConsoleHelper.println("");
    }

    /**
     * The printStarLine method print a line of stars for as banner horizontal line.
     */
    public static void printStarLine() {
        ConsoleHelper.println("********************************************************************************************");
    }

    /**
     * The printPartitionLine method displays a horizontal line for accounts displaying partition.
     */
    public static void printPartitionline() {
        ConsoleHelper.println("--------------------------------------------------------------------------------------------");
    }

    public static void cleanScreen() {
        for (int i = 0; i < 20; i++) {
            ConsoleHelper.println("");
        }
    }



}
