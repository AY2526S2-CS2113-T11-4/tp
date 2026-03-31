package dextro.command;

import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public interface Command {
    CommandResult execute(StudentDatabase db) throws CommandException;

    CommandResult undo(StudentDatabase db) throws CommandException;

    boolean isUndoable();
}
