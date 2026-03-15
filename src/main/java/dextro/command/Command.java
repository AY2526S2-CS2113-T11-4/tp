package dextro.command;

import dextro.model.record.StudentDatabase;

public interface Command {
    CommandResult execute(StudentDatabase db);
}
