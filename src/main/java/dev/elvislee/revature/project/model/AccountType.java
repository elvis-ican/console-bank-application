package dev.elvislee.revature.project.model;

/**
 * The AccountType enum defines three kinds of valid account type.
 */
public enum AccountType {
    SAVING,
    CHECKING,
    JOINT;

    /**
     * The getAccountType method takes an integer and returns the corresponding
     * Account Type.
     *
     * @param i
     */
    public static AccountType getAccountType(Integer i) {
        switch(i) {
            case 1: return SAVING;
            case 2: return CHECKING;
            case 3: return JOINT;
            default: throw new IllegalArgumentException();
        }
    }
}
