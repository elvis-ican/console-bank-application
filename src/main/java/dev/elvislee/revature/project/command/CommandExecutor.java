package dev.elvislee.revature.project.command;

import dev.elvislee.revature.project.exception.CancelOperationException;
import dev.elvislee.revature.project.exception.InterruptedOperationException;
import dev.elvislee.revature.project.model.Operation;
import java.util.HashMap;
import java.util.Map;

/**
 * The BankApp use the Command Design Pattern.
 * <p>
 * The CommandExecutor class provide static method to
 * invoke various operation commands. The operation that will be
 * invoked by the user is defined at the runtime.
 */
public class CommandExecutor {
    private static final Map<Operation, Command> commandsMap;

    static {
        commandsMap = new HashMap<>();
        commandsMap.put(Operation.REGISTER, new RegisterCommand());
        commandsMap.put(Operation.LOGIN, new LoginCommand());
        commandsMap.put(Operation.CREATE, new CreateAccountCommand());
        commandsMap.put(Operation.VIEWBALANCE, new ViewBalanceCommand());
        commandsMap.put(Operation.VIEWTRANSACTIONS, new ViewTransactionsCommand());
        commandsMap.put(Operation.DEPOSIT, new DepositCommand());
        commandsMap.put(Operation.WITHDRAW, new WithdrawCommand());
        commandsMap.put(Operation.TRANSFER, new TransferCommand());
        commandsMap.put(Operation.LOGOUT, new LogoutCommand());
        commandsMap.put(Operation.EXIT, new ExitCommand());
    }

    private CommandExecutor() {
    }

    public static final void execute(Operation operation) throws InterruptedOperationException, CancelOperationException {
        Command command = commandsMap.get(operation);
        command.execute();
    }
}

