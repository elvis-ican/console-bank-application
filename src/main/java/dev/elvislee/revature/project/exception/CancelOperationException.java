package dev.elvislee.revature.project.exception;

/**
 * The empty custom defined CancelOperationException class
 * aims at using the throwable behavior of the Exception class.
 * When a user enter "c" in anywhere that requires user input,
 * exception will be thrown, which will allow the user to return
 * to the menu page. If the user enter "c" in the menu operation
 * input, the BankApp will be terminated.
 */
public class CancelOperationException extends Exception {
}
