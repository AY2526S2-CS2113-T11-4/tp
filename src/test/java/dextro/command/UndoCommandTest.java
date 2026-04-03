package dextro.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

class UndoCommandTest {

    private CommandHistory history;
    private StudentDatabase db;
    private Storage storage;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
    }

    @Test
    void execute_emptyHistory_returnsWarning() throws CommandException {
        UndoCommand cmd = new UndoCommand(history);
        CommandResult result = cmd.execute(db, storage);

        assertEquals("Warning: No command to undo", result.getMessage());
    }

    @Test
    void execute_afterOneCreateCommand_undoesSuccessfully() throws CommandException {
        CreateCommand createCmd = new CreateCommand("Alice", "91234567", "alice@test.com", "123 Street", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertEquals(1, db.getStudentCount());

        UndoCommand undoCmd = new UndoCommand(history);
        CommandResult result = undoCmd.execute(db, storage);

        assertEquals(0, db.getStudentCount());
        assertFalse(result.shouldExit());
    }

    @Test
    void execute_afterMultipleCreateCommands_undoesLastCommand() throws CommandException {
        CreateCommand createCmd1 = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr1", "CS");
        CreateCommand createCmd2 = new CreateCommand("Bob", "98765432", "bob@test.com", "Addr2", "CEG");
        CreateCommand createCmd3 = new CreateCommand("Charlie", "87654321", "charlie@test.com", "Addr3", "ISC");

        createCmd1.execute(db, storage);
        history.push(createCmd1);
        createCmd2.execute(db, storage);
        history.push(createCmd2);
        createCmd3.execute(db, storage);
        history.push(createCmd3);

        assertEquals(3, db.getStudentCount());

        UndoCommand undoCmd = new UndoCommand(history);
        undoCmd.execute(db, storage);

        assertEquals(2, db.getStudentCount());
        assertEquals("Alice", db.getStudent(0).getName());
        assertEquals("Bob", db.getStudent(1).getName());
    }

    @Test
    void execute_multipleUndos_undoesInReverseOrder() throws CommandException {
        CreateCommand createCmd1 = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr1", "CS");
        CreateCommand createCmd2 = new CreateCommand("Bob", "98765432", "bob@test.com", "Addr2", "CEG");

        createCmd1.execute(db, storage);
        history.push(createCmd1);
        createCmd2.execute(db, storage);
        history.push(createCmd2);

        assertEquals(2, db.getStudentCount());

        UndoCommand undo1 = new UndoCommand(history);
        undo1.execute(db, storage);
        assertEquals(1, db.getStudentCount());

        UndoCommand undo2 = new UndoCommand(history);
        undo2.execute(db, storage);
        assertEquals(0, db.getStudentCount());
    }

    @Test
    void execute_undoAfterDelete_restoresStudent() throws CommandException {
        Student student = new Student.Builder("Alice")
                .phone("91234567")
                .email("alice@test.com")
                .build();
        db.addStudent(student);

        DeleteCommand deleteCmd = new DeleteCommand(1);
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(0, db.getStudentCount());

        UndoCommand undoCmd = new UndoCommand(history);
        undoCmd.execute(db, storage);

        assertEquals(1, db.getStudentCount());
        assertEquals("Alice", db.getStudent(0).getName());
    }

    @Test
    void execute_withoutStorage_returnsNull() throws CommandException {
        UndoCommand cmd = new UndoCommand(history);
        CommandResult result = cmd.execute(db);

        assertNull(result);
    }

    @Test
    void isUndoable_always_returnsFalse() {
        UndoCommand cmd = new UndoCommand(history);
        assertFalse(cmd.isUndoable());
    }

    @Test
    void undo_withoutStorage_returnsNull() throws CommandException {
        UndoCommand cmd = new UndoCommand(history);
        CommandResult result = cmd.undo(db);

        assertNull(result);
    }

    @Test
    void undo_withStorage_throwsCommandException() {
        UndoCommand cmd = new UndoCommand(history);
        CommandException exception = assertThrows(CommandException.class, () ->
            cmd.undo(db, storage)
        );

        assertEquals("Cannot undo an undo command", exception.getMessage());
    }

    @Test
    void execute_historyEmptiesAfterUndo_popsFromStack() throws CommandException {
        CreateCommand createCmd = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertFalse(history.isEmpty());

        UndoCommand undoCmd = new UndoCommand(history);
        undoCmd.execute(db, storage);

        assertTrue(history.isEmpty());
    }

    @Test
    void execute_undoNonUndoableCommand_throwsException() {
        StatusCommand statusCmd = new StatusCommand(1);
        history.push(statusCmd);

        UndoCommand undoCmd = new UndoCommand(history);
        assertThrows(CommandException.class, () ->
            undoCmd.execute(db, storage)
        );
    }

    @Test
    void execute_consecutiveUndoOnEmptyHistory_returnsWarningEachTime() throws CommandException {
        UndoCommand undo1 = new UndoCommand(history);
        CommandResult result1 = undo1.execute(db, storage);
        assertEquals("Warning: No command to undo", result1.getMessage());

        UndoCommand undo2 = new UndoCommand(history);
        CommandResult result2 = undo2.execute(db, storage);
        assertEquals("Warning: No command to undo", result2.getMessage());
    }

    @Test
    void execute_afterClearingHistory_returnsWarning() throws CommandException {
        CreateCommand createCmd = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        history.clear();

        UndoCommand undoCmd = new UndoCommand(history);
        CommandResult result = undoCmd.execute(db, storage);

        assertEquals("Warning: No command to undo", result.getMessage());
    }

    @Test
    void execute_undoMultipleDeleteCommands_restoresInReverseOrder() throws CommandException {
        Student student1 = new Student.Builder("Alice").build();
        Student student2 = new Student.Builder("Bob").build();
        Student student3 = new Student.Builder("Charlie").build();

        db.addStudent(student1);
        db.addStudent(student2);
        db.addStudent(student3);

        DeleteCommand delete1 = new DeleteCommand(3);
        delete1.execute(db, storage);
        history.push(delete1);

        DeleteCommand delete2 = new DeleteCommand(2);
        delete2.execute(db, storage);
        history.push(delete2);

        assertEquals(1, db.getStudentCount());

        UndoCommand undo1 = new UndoCommand(history);
        undo1.execute(db, storage);
        assertEquals(2, db.getStudentCount());

        UndoCommand undo2 = new UndoCommand(history);
        undo2.execute(db, storage);
        assertEquals(3, db.getStudentCount());
    }

    @Test
    void execute_mixedUndoableCommands_undoesCorrectly() throws CommandException {
        CreateCommand create = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr", "CS");
        create.execute(db, storage);
        history.push(create);

        DeleteCommand delete = new DeleteCommand(1);
        delete.execute(db, storage);
        history.push(delete);

        assertEquals(0, db.getStudentCount());

        UndoCommand undo1 = new UndoCommand(history);
        undo1.execute(db, storage);
        assertEquals(1, db.getStudentCount());

        UndoCommand undo2 = new UndoCommand(history);
        undo2.execute(db, storage);
        assertEquals(0, db.getStudentCount());
    }

    @Test
    void execute_commandThatThrowsExceptionOnUndo_propagatesException() {
        Command failingCommand = new Command() {
            @Override
            public CommandResult execute(StudentDatabase db) throws CommandException {
                return null;
            }

            @Override
            public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
                return new CommandResult("Executed");
            }

            @Override
            public CommandResult undo(StudentDatabase db) throws CommandException {
                return null;
            }

            @Override
            public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
                throw new CommandException("Undo failed intentionally");
            }

            @Override
            public boolean isUndoable() {
                return true;
            }
        };

        history.push(failingCommand);

        UndoCommand undoCmd = new UndoCommand(history);
        CommandException exception = assertThrows(CommandException.class, () ->
            undoCmd.execute(db, storage)
        );

        assertEquals("Undo failed intentionally", exception.getMessage());
    }

    @Test
    void execute_longChainOfCommands_undoesAllSuccessfully() throws CommandException {
        int commandCount = 50;

        for (int i = 0; i < commandCount; i++) {
            CreateCommand cmd = new CreateCommand("Student" + i, "91234567", "test@test.com", "Addr", "CS");
            cmd.execute(db, storage);
            history.push(cmd);
        }

        assertEquals(commandCount, db.getStudentCount());

        for (int i = 0; i < commandCount; i++) {
            UndoCommand undoCmd = new UndoCommand(history);
            undoCmd.execute(db, storage);
        }

        assertEquals(0, db.getStudentCount());
    }

    @Test
    void execute_afterPartialUndoSequence_maintainsCorrectState() throws CommandException {
        CreateCommand cmd1 = new CreateCommand("Alice", "91234567", "alice@test.com", "Addr", "CS");
        CreateCommand cmd2 = new CreateCommand("Bob", "98765432", "bob@test.com", "Addr", "CEG");
        CreateCommand cmd3 = new CreateCommand("Charlie", "87654321", "charlie@test.com", "Addr", "ISC");

        cmd1.execute(db, storage);
        history.push(cmd1);
        cmd2.execute(db, storage);
        history.push(cmd2);
        cmd3.execute(db, storage);
        history.push(cmd3);

        UndoCommand undo1 = new UndoCommand(history);
        undo1.execute(db, storage);

        assertEquals(2, db.getStudentCount());
        assertEquals("Alice", db.getStudent(0).getName());
        assertEquals("Bob", db.getStudent(1).getName());
    }
}
