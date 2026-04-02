package dextro.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

class UndoCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private CommandHistory history;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
        history = new CommandHistory();
    }

    @Test
    void execute_emptyHistory_returnsWarning() {
        // Arrange
        UndoCommand cmd = new UndoCommand(history);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Warning: No command to undo", result.getMessage(),
                "Should return warning message for empty history");
        assertFalse(result.shouldExit(), "Should not exit");
    }

    @Test
    void execute_undoCreateCommand_studentRemoved() {
        // Arrange
        CreateCommand createCmd = new CreateCommand(
                "John Doe", "91234567", "john@example.com", "123 Street", "CS"
        );
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertEquals(1, db.getStudentCount(), "Student should be created");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, db.getStudentCount(), "Student should be removed after undo");
        assertTrue(result.getMessage().contains("Undone"),
                "Message should indicate undo operation");
        assertTrue(result.getMessage().contains("John Doe"),
                "Message should contain student name");
        assertTrue(history.isEmpty(), "History should be empty after undo");
    }

    @Test
    void execute_undoDeleteCommand_studentRestored() {
        // Arrange
        Student student = new Student.Builder("Alice Tan")
                .phone("98765432")
                .email("alice@example.com")
                .address("456 Avenue")
                .course("CEG")
                .build();
        db.addStudent(student);

        assertEquals(1, db.getStudentCount(), "Student should exist initially");

        DeleteCommand deleteCmd = new DeleteCommand(1);
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(0, db.getStudentCount(), "Student should be deleted");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, db.getStudentCount(), "Student should be restored after undo");
        assertTrue(result.getMessage().contains("Undone"),
                "Message should indicate undo operation");
        assertTrue(result.getMessage().contains("Alice Tan"),
                "Message should contain student name");
        assertTrue(history.isEmpty(), "History should be empty after undo");

        Student restoredStudent = db.getStudent(0);
        assertEquals("Alice Tan", restoredStudent.getName(),
                "Restored student should have correct name");
    }

    @Test
    void execute_multipleCommands_undosMostRecent() {
        // Arrange
        CreateCommand cmd1 = new CreateCommand("Student1", "11111111", "s1@example.com", "Addr1", "CS");
        CreateCommand cmd2 = new CreateCommand("Student2", "22222222", "s2@example.com", "Addr2", "CEG");
        CreateCommand cmd3 = new CreateCommand("Student3", "33333333", "s3@example.com", "Addr3", "EE");

        cmd1.execute(db, storage);
        history.push(cmd1);
        cmd2.execute(db, storage);
        history.push(cmd2);
        cmd3.execute(db, storage);
        history.push(cmd3);

        assertEquals(3, db.getStudentCount(), "Should have 3 students");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertEquals(2, db.getStudentCount(), "Should have 2 students after undo");
        assertTrue(result.getMessage().contains("Student3"),
                "Should undo most recent command (Student3)");

        Student lastStudent = db.getStudent(db.getStudentCount() - 1);
        assertEquals("Student2", lastStudent.getName(),
                "Last student should now be Student2");
    }

    @Test
    void execute_multipleUndos_worksInSequence() {
        // Arrange
        CreateCommand cmd1 = new CreateCommand("First", "11111111", "f@example.com", "Addr1", "CS");
        CreateCommand cmd2 = new CreateCommand("Second", "22222222", "s@example.com", "Addr2", "CEG");
        CreateCommand cmd3 = new CreateCommand("Third", "33333333", "t@example.com", "Addr3", "EE");

        cmd1.execute(db, storage);
        history.push(cmd1);
        cmd2.execute(db, storage);
        history.push(cmd2);
        cmd3.execute(db, storage);
        history.push(cmd3);

        assertEquals(3, db.getStudentCount(), "Should have 3 students initially");

        // Act & Assert - First undo
        UndoCommand undo1 = new UndoCommand(history);
        CommandResult result1 = undo1.execute(db, storage);
        assertEquals(2, db.getStudentCount(), "Should have 2 students after first undo");
        assertTrue(result1.getMessage().contains("Third"), "First undo should remove Third");

        // Act & Assert - Second undo
        UndoCommand undo2 = new UndoCommand(history);
        CommandResult result2 = undo2.execute(db, storage);
        assertEquals(1, db.getStudentCount(), "Should have 1 student after second undo");
        assertTrue(result2.getMessage().contains("Second"), "Second undo should remove Second");

        // Act & Assert - Third undo
        UndoCommand undo3 = new UndoCommand(history);
        CommandResult result3 = undo3.execute(db, storage);
        assertEquals(0, db.getStudentCount(), "Should have 0 students after third undo");
        assertTrue(result3.getMessage().contains("First"), "Third undo should remove First");

        // Act & Assert - Fourth undo on empty history
        UndoCommand undo4 = new UndoCommand(history);
        CommandResult result4 = undo4.execute(db, storage);
        assertEquals("Warning: No command to undo", result4.getMessage(),
                "Fourth undo should return warning");
        assertEquals(0, db.getStudentCount(), "Should still have 0 students");
    }

    @Test
    void execute_mixedCommands_undosCorrectly() {
        // Arrange
        CreateCommand createCmd = new CreateCommand("Bob", "91234567", "bob@example.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertEquals(1, db.getStudentCount(), "Should have 1 student after create");

        DeleteCommand deleteCmd = new DeleteCommand(1);
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(0, db.getStudentCount(), "Should have 0 students after delete");

        // Act - Undo delete
        UndoCommand undo1 = new UndoCommand(history);
        CommandResult result1 = undo1.execute(db, storage);

        // Assert
        assertEquals(1, db.getStudentCount(), "Student should be restored after undoing delete");
        assertTrue(result1.getMessage().contains("Bob"), "Should restore Bob");

        // Act - Undo create
        UndoCommand undo2 = new UndoCommand(history);
        CommandResult result2 = undo2.execute(db, storage);

        // Assert
        assertEquals(0, db.getStudentCount(), "Student should be removed after undoing create");
        assertTrue(result2.getMessage().contains("Bob"), "Should remove Bob");
    }

    @Test
    void execute_withoutStorage_returnsNull() {
        // Arrange
        CreateCommand createCmd = new CreateCommand("Test", "12345678", "test@example.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db);

        // Assert
        assertNull(result, "execute without storage should return null");
    }

    @Test
    void undo_withStorage_throwsException() {
        // Arrange
        UndoCommand cmd = new UndoCommand(history);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.undo(db, storage),
                "Undo should throw exception");

        assertEquals("Cannot undo an undo command", exception.getMessage(),
                "Exception message should indicate cannot undo");
    }

    @Test
    void undo_withoutStorage_returnsNull() {
        // Arrange
        UndoCommand cmd = new UndoCommand(history);

        // Act
        CommandResult result = cmd.undo(db);

        // Assert
        assertNull(result, "undo without storage should return null");
    }

    @Test
    void isUndoable_returnsFalse() {
        // Arrange
        UndoCommand cmd = new UndoCommand(history);

        // Act & Assert
        assertFalse(cmd.isUndoable(), "UndoCommand should not be undoable");
    }

    @Test
    void execute_statusCommandInHistory_throwsException() {
        // Arrange
        Student student = new Student.Builder("Test Student").course("CS").build();
        db.addStudent(student);

        StatusCommand statusCmd = new StatusCommand(1);
        history.push(statusCmd);

        UndoCommand undoCmd = new UndoCommand(history);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> undoCmd.execute(db, storage),
                "Undoing a non-undoable command should throw exception");

        assertEquals("Cannot undo status command", exception.getMessage(),
                "Exception should be from StatusCommand.undo()");
    }

    @Test
    void execute_undoCommandInHistory_throwsException() {
        // Arrange
        UndoCommand innerUndo = new UndoCommand(new CommandHistory());
        history.push(innerUndo);

        UndoCommand outerUndo = new UndoCommand(history);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> outerUndo.execute(db, storage),
                "Undoing an undo command should throw exception");

        assertEquals("Cannot undo an undo command", exception.getMessage(),
                "Exception should indicate cannot undo undo command");
    }

    @Test
    void execute_afterHistoryClear_noCommandToUndo() {
        // Arrange
        CreateCommand createCmd = new CreateCommand("Test", "12345678", "test@example.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertFalse(history.isEmpty(), "History should have command");

        history.clear();

        assertTrue(history.isEmpty(), "History should be empty after clear");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertEquals("Warning: No command to undo", result.getMessage(),
                "Should return warning after history is cleared");
    }

    @Test
    void execute_deleteFromMultipleStudents_undosCorrectly() {
        // Arrange
        Student student1 = new Student.Builder("Alice").course("CS").build();
        Student student2 = new Student.Builder("Bob").course("CEG").build();
        Student student3 = new Student.Builder("Charlie").course("EE").build();

        db.addStudent(student1);
        db.addStudent(student2);
        db.addStudent(student3);

        assertEquals(3, db.getStudentCount(), "Should have 3 students");

        DeleteCommand deleteCmd = new DeleteCommand(2); // Delete Bob
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(2, db.getStudentCount(), "Should have 2 students after delete");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertEquals(3, db.getStudentCount(), "Should have 3 students after undo");
        assertTrue(result.getMessage().contains("Bob"), "Should restore Bob");

        // Verify Bob is restored
        boolean bobFound = false;
        for (int i = 0; i < db.getStudentCount(); i++) {
            if (db.getStudent(i).getName().equals("Bob")) {
                bobFound = true;
                break;
            }
        }
        assertTrue(bobFound, "Bob should be found in database after undo");
    }

    @Test
    void execute_createThenDeleteSameStudent_undoDeleteRestores() {
        // Arrange
        CreateCommand createCmd = new CreateCommand("Diana", "91234567", "diana@example.com", "Addr", "CS");
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertEquals(1, db.getStudentCount(), "Should have 1 student after create");

        DeleteCommand deleteCmd = new DeleteCommand(1);
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(0, db.getStudentCount(), "Should have 0 students after delete");

        UndoCommand undoDeleteCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoDeleteCmd.execute(db, storage);

        // Assert
        assertEquals(1, db.getStudentCount(), "Should have 1 student after undoing delete");
        assertTrue(result.getMessage().contains("Diana"), "Should restore Diana");

        Student restoredStudent = db.getStudent(0);
        assertEquals("Diana", restoredStudent.getName(), "Restored student should be Diana");
        assertEquals("CS", restoredStudent.getCourse(), "Restored student should have correct course");
    }

    @Test
    void execute_historyPreservesExecutionOrder() {
        // Arrange
        CreateCommand cmd1 = new CreateCommand("First", "11111111", "f@example.com", "A1", "CS");
        CreateCommand cmd2 = new CreateCommand("Second", "22222222", "s@example.com", "A2", "CEG");
        CreateCommand cmd3 = new CreateCommand("Third", "33333333", "t@example.com", "A3", "EE");
        CreateCommand cmd4 = new CreateCommand("Fourth", "44444444", "fo@example.com", "A4", "ME");

        cmd1.execute(db, storage);
        history.push(cmd1);
        cmd2.execute(db, storage);
        history.push(cmd2);
        cmd3.execute(db, storage);
        history.push(cmd3);
        cmd4.execute(db, storage);
        history.push(cmd4);

        // Act & Assert - Undo in reverse order
        UndoCommand undo1 = new UndoCommand(history);
        CommandResult result1 = undo1.execute(db, storage);
        assertTrue(result1.getMessage().contains("Fourth"), "First undo should be Fourth");

        UndoCommand undo2 = new UndoCommand(history);
        CommandResult result2 = undo2.execute(db, storage);
        assertTrue(result2.getMessage().contains("Third"), "Second undo should be Third");

        UndoCommand undo3 = new UndoCommand(history);
        CommandResult result3 = undo3.execute(db, storage);
        assertTrue(result3.getMessage().contains("Second"), "Third undo should be Second");

        UndoCommand undo4 = new UndoCommand(history);
        CommandResult result4 = undo4.execute(db, storage);
        assertTrue(result4.getMessage().contains("First"), "Fourth undo should be First");

        assertEquals(0, db.getStudentCount(), "All students should be removed");
        assertTrue(history.isEmpty(), "History should be empty");
    }

    @Test
    void execute_emptyHistoryDoesNotModifyDatabase() {
        // Arrange
        Student student = new Student.Builder("Permanent").course("CS").build();
        db.addStudent(student);

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertEquals(1, db.getStudentCount(), "Database should remain unchanged");
        assertEquals("Warning: No command to undo", result.getMessage());
        assertEquals("Permanent", db.getStudent(0).getName(),
                "Existing student should remain unchanged");
    }

    @Test
    void execute_undoAfterCreateFailure_historyManagement() {
        // Arrange - This test ensures that only successful commands are in history
        CreateCommand successCmd = new CreateCommand("Success", "11111111", "s@example.com", "Addr", "CS");
        successCmd.execute(db, storage);
        history.push(successCmd);

        assertEquals(1, db.getStudentCount(), "Should have 1 student");
        assertFalse(history.isEmpty(), "History should have 1 command");

        UndoCommand undoCmd = new UndoCommand(history);

        // Act
        CommandResult result = undoCmd.execute(db, storage);

        // Assert
        assertEquals(0, db.getStudentCount(), "Student should be removed");
        assertTrue(history.isEmpty(), "History should be empty after undo");
        assertTrue(result.getMessage().contains("Success"), "Should undo Success");
    }

    @Test
    void constructor_nullHistory_createsValidCommand() {
        // Arrange & Act
        CommandHistory nullHistory = null;
        UndoCommand cmd = new UndoCommand(nullHistory);

        // Assert
        assertNotNull(cmd, "UndoCommand should be created even with null history");
    }

    @Test
    void execute_complexScenario_multipleCreatesAndDeletes() {
        // Arrange - Complex scenario
        CreateCommand create1 = new CreateCommand("Alpha", "11111111", "a@example.com", "Addr1", "CS");
        create1.execute(db, storage);
        history.push(create1);

        CreateCommand create2 = new CreateCommand("Beta", "22222222", "b@example.com", "Addr2", "CEG");
        create2.execute(db, storage);
        history.push(create2);

        DeleteCommand delete1 = new DeleteCommand(1); // Delete Alpha
        delete1.execute(db, storage);
        history.push(delete1);

        CreateCommand create3 = new CreateCommand("Gamma", "33333333", "g@example.com", "Addr3", "EE");
        create3.execute(db, storage);
        history.push(create3);

        assertEquals(2, db.getStudentCount(), "Should have Beta and Gamma");

        // Act - Undo Gamma creation
        UndoCommand undo1 = new UndoCommand(history);
        CommandResult result1 = undo1.execute(db, storage);

        // Assert
        assertEquals(1, db.getStudentCount(), "Should have only Beta");
        assertTrue(result1.getMessage().contains("Gamma"));

        // Act - Undo Alpha deletion (restore Alpha)
        UndoCommand undo2 = new UndoCommand(history);
        CommandResult result2 = undo2.execute(db, storage);

        // Assert
        assertEquals(2, db.getStudentCount(), "Should have Beta and Alpha");
        assertTrue(result2.getMessage().contains("Alpha"));

        // Act - Undo Beta creation
        UndoCommand undo3 = new UndoCommand(history);
        CommandResult result3 = undo3.execute(db, storage);

        // Assert
        assertEquals(1, db.getStudentCount(), "Should have only Alpha");
        assertTrue(result3.getMessage().contains("Beta"));

        // Act - Undo Alpha creation
        UndoCommand undo4 = new UndoCommand(history);
        CommandResult result4 = undo4.execute(db, storage);

        // Assert
        assertEquals(0, db.getStudentCount(), "Should have no students");
        assertTrue(result4.getMessage().contains("Alpha"));
        assertTrue(history.isEmpty(), "History should be empty");
    }
}
