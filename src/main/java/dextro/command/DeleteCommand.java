package dextro.command;

import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class DeleteCommand implements Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        try {
            // Subtract 1 to convert the 1-based user input to the 0-based ArrayList index
            Student deletedStudent = db.removeStudent(index - 1);
            return new CommandResult("Successfully deleted student:\n" + deletedStudent.toString());

        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("The student at index " + index + " does not exist.");
        }
    }
}