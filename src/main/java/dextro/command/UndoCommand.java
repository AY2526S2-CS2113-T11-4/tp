package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public class UndoCommand implements Command {
    private final CommandHistory history;

    public UndoCommand(CommandHistory history) {
        assert history != null : "CommandHistory should not be null in constructor";
        this.history = history;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        assert db != null : "StudentDatabase should not be null";
        assert storage != null : "Storage should not be null";
        assert history != null : "CommandHistory should not be null";

        if (history.isEmpty()) {
            return new CommandResult("Warning: No command to undo");
        }

        Command lastCommand = history.pop();
        assert lastCommand != null : "Last command should not be null";
        assert lastCommand.isUndoable() : "Command should be undoable";

        CommandResult result = lastCommand.undo(db, storage);
        assert result != null : "Undo result should not be null";
        return result;
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        throw new CommandException("Cannot undo an undo command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
