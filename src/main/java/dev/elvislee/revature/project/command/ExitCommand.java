package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.util.ConsoleHelper;

public class ExitCommand implements Command{

    /**
     * The ExitCommand class execute display exit information to user.
     */
    @Override
    public int execute() throws InterruptedOperationException {
        int success;
        ConsoleHelper.println("");
        ConsoleHelper.printExitMessage();
        success = 1;
        return success;
    }
}
