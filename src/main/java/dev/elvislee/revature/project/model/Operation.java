package dev.elvislee.revature.project.model;

/**
 * The Operation enum defines all command operations available from the
 * application.
 */
public enum Operation {
    LOGIN,
    CREATE,
    VIEWBALANCE,
    VIEWTRANSACTIONS,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    LOGOUT,
    REGISTER,
    EXIT;

    /**
     * The getOperation method takes an integer and returns the corresponding
     * Command Operation Type.
     *
     * @param i
     */
    public static Operation getOperation(Integer i) {
        switch(i) {
            case 0: return LOGIN;
            case 1: return CREATE;
            case 2: return VIEWBALANCE;
            case 3: return VIEWTRANSACTIONS;
            case 4: return DEPOSIT;
            case 5: return WITHDRAW;
            case 6: return TRANSFER;
            case 7: return LOGOUT;
            case 8: return REGISTER;
            case 9: return EXIT;
            default: throw new IllegalArgumentException();
        }
    }
}
