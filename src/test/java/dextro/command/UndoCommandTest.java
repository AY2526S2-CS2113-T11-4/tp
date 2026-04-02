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

/**
 * Tests the undo functionality to ensure commands are reversed in correct LIFO order.
 */
class UndoCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private CommandHistory history;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        // Use a temporary path for testing to avoid overwriting real data
        storage = new Storage("./data/test_DextroStudentList.txt");
        history = new CommandHistory();
    }

    @Test
    void execute_emptyHistory_returnsWarning() {
        UndoCommand undoCmd = new UndoCommand(history);
        CommandResult result = undoCmd.execute(db, storage);

        assertNotNull(result);
        assertEquals("Warning: No command to undo", result.getMessage());
        assertFalse(result.shouldExit());
    }

    @Test
    void execute_undoCreateCommand_studentRemoved() {
        CreateCommand createCmd = new CreateCommand(
                "John Doe", "91234567", "john@example.com", "123 Street", "CS"
        );
        createCmd.execute(db, storage);
        history.push(createCmd);

        assertEquals(1, db.getStudentCount());

        UndoCommand undoCmd = new UndoCommand(history);
        CommandResult result = undoCmd.execute(db, storage);

        assertEquals(0, db.getStudentCount(), "Student should be removed after undo");
        assertTrue(result.getMessage().contains("John Doe"));
        assertTrue(history.isEmpty());
    }

    @Test
    void execute_undoDeleteCommand_studentRestored() {
        Student student = new Student.Builder("Alice Tan").course("CEG").build();
        db.addStudent(student);

        // Assume index 1 for the first student in your implementation
        DeleteCommand deleteCmd = new DeleteCommand(1);
        deleteCmd.execute(db, storage);
        history.push(deleteCmd);

        assertEquals(0, db.getStudentCount());

        UndoCommand undoCmd = new UndoCommand(history);
        undoCmd.execute(db, storage);

        assertEquals(1, db.getStudentCount(), "Student should be restored");
        assertEquals("Alice Tan", db.getStudent(0).getName());
    }

    @Test
    void execute_multipleUndos_worksInSequence() {
        // Push 3 commands
        for (int i = 1; i <= 3; i++) {
            CreateCommand cmd = new CreateCommand("S" + i, "000", "e@e.com", "A", "CS");
            cmd.execute(db, storage);
            history.push(cmd);
        }

        assertEquals(3, db.getStudentCount());

        // Perform 3 undos
        new UndoCommand(history).execute(db, storage);
        assertEquals(2, db.getStudentCount());

        new UndoCommand(history).execute(db, storage);
        assertEquals(1, db.getStudentCount());

        new UndoCommand(history).execute(db, storage);
        assertEquals(0, db.getStudentCount());
    }

    @Test
    void execute_mixedCommands_undosCorrectly() {
        CreateCommand create = new CreateCommand("Bob", "9123", "b@b.com", "A", "CS");
        create.execute(db, storage);
        history.push(create);

        DeleteCommand delete = new DeleteCommand(1);
        delete.execute(db, storage);
        history.push(delete);

        // Undo Delete
        new UndoCommand(history).execute(db, storage);
        assertEquals(1, db.getStudentCount());
        assertEquals("Bob", db.getStudent(0).getName());

        // Undo Create
        new UndoCommand(history).execute(db, storage);
        assertEquals(0, db.getStudentCount());
    }

    @Test
    void isUndoable_returnsFalse() {
        UndoCommand cmd = new UndoCommand(history);
        assertFalse(cmd.isUndoable(), "UndoCommand itself should not be added to history");
    }

    /**
     * FIX: The failing complex scenario.
     * Logic: Create(A), Create(B), Delete(A), Create(G).
     * Undo sequence should be: Remove G -> Restore A -> Remove B -> Remove A.
     */
    @Test
    void execute_complexScenario_multipleCreatesAndDeletes() {
        // 1. Create Alpha
        CreateCommand c1 = new CreateCommand("Alpha", "1", "a@a.com", "A1", "CS");
        c1.execute(db, storage);
        history.push(c1);

        // 2. Create Beta
        CreateCommand c2 = new CreateCommand("Beta", "2", "b@b.com", "A2", "CEG");
        c2.execute(db, storage);
        history.push(c2);

        // 3. Delete Alpha (Index 1)
        DeleteCommand d1 = new DeleteCommand(1);
        d1.execute(db, storage);
        history.push(d1);

        // 4. Create Gamma
        CreateCommand c3 = new CreateCommand("Gamma", "3", "g@g.com", "A3", "EE");
        c3.execute(db, storage);
        history.push(c3);

        // Current DB: [Beta, Gamma] (Assuming Alpha was index 1 and removed)
        assertEquals(2, db.getStudentCount());

        // Undo 1: Remove Gamma
        CommandResult r1 = new UndoCommand(history).execute(db, storage);
        assertTrue(r1.getMessage().contains("Gamma"), "Should undo Gamma creation");
        assertEquals(1, db.getStudentCount());

        // Undo 2: Restore Alpha
        CommandResult r2 = new UndoCommand(history).execute(db, storage);
        assertTrue(r2.getMessage().contains("Alpha"), "Should undo Alpha deletion");
        assertEquals(2, db.getStudentCount(), "Alpha and Beta should be present");

        // Undo 3: Remove Beta
        CommandResult r3 = new UndoCommand(history).execute(db, storage);
        assertTrue(r3.getMessage().contains("Beta"), "Should undo Beta creation");
        assertEquals(1, db.getStudentCount());

        // Undo 4: Remove Alpha
        CommandResult r4 = new UndoCommand(history).execute(db, storage);
        assertTrue(r4.getMessage().contains("Alpha"), "Should undo Alpha creation");
        assertEquals(0, db.getStudentCount());
        assertTrue(history.isEmpty());
    }

    @Test
    void undo_withStorage_throwsException() {
        UndoCommand cmd = new UndoCommand(history);
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void execute_withoutStorage_returnsNull() {
        UndoCommand cmd = new UndoCommand(history);
        assertNull(cmd.execute(db));
    }
}