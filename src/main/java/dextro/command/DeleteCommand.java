package dextro.command;

import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public class DeleteCommand implements Command{
    private int index;

    public DeleteCommand(int index){
        this.index = index;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        String studentInfo;
        try {
            studentInfo = db.getStudent(index - 1).getName();
            db.removeStudent(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Please choose an index within range of student list");
        }
        String message = String.format("Student removed: %s", studentInfo);
        return new CommandResult(message);
    }
}
