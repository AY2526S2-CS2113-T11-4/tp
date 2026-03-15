package dextro.command.module;

import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.model.record.StudentDatabase;

public class RemoveCommand implements Command {
    public RemoveCommand(int index, String moduleCode) {
    }

    @Override
    public CommandResult execute(StudentDatabase db) {
        return null;
    }
}
