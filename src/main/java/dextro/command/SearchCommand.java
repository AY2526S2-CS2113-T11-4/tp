package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

import java.util.List;

public class SearchCommand implements Command {

    private final String course;
    private final String moduleCode;
    private final String phone;

    public SearchCommand(String course, String moduleCode, String phone) {
        this.course = course;
        this.moduleCode = moduleCode;
        this.phone = phone;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        List<Student> students = db.getAllStudents();
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            int originalIndex = i + 1; // 1-based index for display

            if (course != null) {
                if (student.getCourse() != null && student.getCourse().toLowerCase().contains(course.toLowerCase())) {
                    sb.append(originalIndex).append(". ")
                            .append(student.getName()).append(", ")
                            .append(student.getCourse()).append("\n");
                    found = true;
                }
            } else if (moduleCode != null) {
                for (Module m : student.getModules()) {
                    if (m.getCode().toLowerCase().contains(moduleCode.toLowerCase())) {
                        sb.append(originalIndex).append(". ")
                                .append(student.getName()).append(", ")
                                .append(m.getCode()).append(": ")
                                .append(m.getGrade().toString()).append(System.lineSeparator());
                        found = true;
                    }
                }
            } else if (phone != null) {
                if (student.getPhone() != null && student.getPhone().contains(phone)) {
                    sb.append(originalIndex).append(". ")
                            .append(student.getName()).append(", ")
                            .append(student.getPhone()).append(System.lineSeparator());
                    found = true;
                }
            }
        }

        if (!found) {
            return new CommandResult("No matching students found.");
        }

        return new CommandResult(sb.toString().trim(), false);
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        // Routed to main logic, passing null for storage since SearchCommand doesn't use it
        return this.execute(db, null);
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        throw new CommandException("Cannot undo search command");
    }


    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        return null;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
