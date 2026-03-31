package dextro.command;

import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public class ExitCommand implements Command{
    public ExitCommand() {

    }

    public CommandResult execute(StudentDatabase studentDatabase) {
        return new CommandResult("Goodbye!", true);
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        throw new CommandException("Cannot undo exit command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
