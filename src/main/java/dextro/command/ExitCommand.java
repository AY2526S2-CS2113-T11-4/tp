package dextro.command;

import dextro.model.record.StudentDatabase;

public class ExitCommand implements Command{
    public ExitCommand() {

    }

    public CommandResult execute(StudentDatabase studentDatabase) {
        return new CommandResult("Goodbye!", true);
    }
}
