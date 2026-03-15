package dextro.command;

import dextro.model.record.StudentDatabase;

public class DeleteCommand implements Command{
    public DeleteCommand(int index) {
    }

    @Override
    public CommandResult execute(StudentDatabase db) {
        return null;
    }
}
