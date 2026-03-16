package dextro.command;

public class CommandResult {

    private final String message;
    private final boolean exit;

    public CommandResult(String message) {
        this(message, false);
    }

    /**
     * Constructs a new CommandResult.
     *
     * @param message the message to be returned to the user after executing the command
     * @param exit    true if this command should terminate the application, false otherwise
     */
    public CommandResult(String message, boolean exit) {
        this.message = message;
        this.exit = exit;
    }

    public String getMessage() {
        return message;
    }

    public boolean shouldExit() {
        return exit;
    }
}
