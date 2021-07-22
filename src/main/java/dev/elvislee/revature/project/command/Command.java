package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.exception.InterruptedOperationException;

public interface Command {
    int execute() throws InterruptedOperationException, CancelOperationException;
}
