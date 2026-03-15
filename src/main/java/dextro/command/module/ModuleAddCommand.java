package dextro.command.module;

import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.model.record.StudentDatabase;

public class ModuleAddCommand implements Command {
    public ModuleAddCommand(int index, String moduleGrade) {
    }

    @Override
    public CommandResult execute(StudentDatabase db) {
        return null;
    }
}
