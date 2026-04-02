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
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

class StatusCommandTest {

    private StudentDatabase db;
    private Storage storage;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
    }

    @Test
    void execute_validStudentNoModules_returnsCorrectStatus() {
        // Arrange
        Student student = new Student.Builder("Alice Tan")
                .phone("91234567")
                .email("alice@example.com")
                .address("123 Kent Ridge")
                .course("CS")
                .build();
        db.addStudent(student);

        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.shouldExit(), "Status command should not exit");

        String message = result.getMessage();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("Index 1:"), "Message should contain index");
        assertTrue(message.contains("Alice Tan"), "Message should contain student name");
        assertTrue(message.contains("CS"), "Message should contain course");
        assertTrue(message.contains("Cap 0.0"), "CAP should be 0.0 for no modules");
        assertTrue(message.contains("0/160 MCs"), "Should show 0 MCs");
        assertTrue(message.contains("Status: Just Started"), "Status should be 'Just Started'");

        assertEquals("Index 1: Alice Tan, CS, Cap 0.0, 0/160 MCs completed. Status: Just Started.",
                message, "Full message should match expected format");
    }

    @Test
    void execute_studentWithModules_calculatesCorrectCap() {
        // Arrange
        Student student = new Student.Builder("Bob Lee")
                .phone("98765432")
                .email("bob@example.com")
                .address("456 Engineering Drive")
                .course("CEG")
                .build();

        student.addModule(new Module("CS2103T", Grade.A));
        student.addModule(new Module("CS2101", Grade.A_MINUS));
        student.addModule(new Module("CS2100", Grade.B_PLUS));

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertNotNull(result, "Result should not be null");

        double expectedCap = (5.0 + 4.5 + 4.0) / 3.0;
        String message = result.getMessage();
        assertTrue(message.contains(String.format("Cap %.1f", expectedCap)),
                "CAP should be correctly calculated");
        assertTrue(message.contains("12/160 MCs"), "Should show 12 MCs (3 modules * 4 MC)");
        assertTrue(message.contains("Status: Just Started"), "Status should be 'Just Started' for 12 MCs");
    }

    @Test
    void execute_studentWithManyModules_correctProgressStatus() {
        // Arrange - student with 20 modules (80 MCs)
        Student student = new Student.Builder("Charlie Ng")
                .phone("87654321")
                .email("charlie@example.com")
                .address("789 Computing Drive")
                .course("CS")
                .build();

        for (int i = 0; i < 20; i++) {
            student.addModule(new Module("CS" + (2100 + i), Grade.B));
        }

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("80/160 MCs"), "Should show 80 MCs");
        assertTrue(message.contains("Status: Satisfactory"),
                "Status should be 'Satisfactory' for 80 MCs");
        assertTrue(message.contains("Cap 3.5"), "CAP should be 3.5 for all B grades");
    }

    @Test
    void execute_studentOnTrack_correctStatus() {
        // Arrange - student with 15 modules (60 MCs)
        Student student = new Student.Builder("David Lim")
                .course("CEG")
                .build();

        for (int i = 0; i < 15; i++) {
            student.addModule(new Module("EE" + (2000 + i), Grade.A));
        }

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("60/160 MCs"), "Should show 60 MCs");
        assertTrue(message.contains("Status: On Track"), "Status should be 'On Track' for 60 MCs");
    }

    @Test
    void execute_studentGoodProgress_correctStatus() {
        // Arrange - student with 35 modules (140 MCs)
        Student student = new Student.Builder("Emma Wong")
                .course("CS")
                .build();

        for (int i = 0; i < 35; i++) {
            student.addModule(new Module("CS" + (3000 + i), Grade.A_PLUS));
        }

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("140/160 MCs"), "Should show 140 MCs");
        assertTrue(message.contains("Status: Good Progress"),
                "Status should be 'Good Progress' for 140 MCs");
        assertTrue(message.contains("Cap 5.0"), "CAP should be 5.0 for all A+ grades");
    }

    @Test
    void execute_studentCompleted_correctStatus() {
        // Arrange - student with 40 modules (160 MCs)
        Student student = new Student.Builder("Frank Chen")
                .course("CS")
                .build();

        for (int i = 0; i < 40; i++) {
            student.addModule(new Module("CS" + (4000 + i), Grade.A));
        }

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("160/160 MCs"), "Should show 160 MCs");
        assertTrue(message.contains("Status: Completed"), "Status should be 'Completed' for 160 MCs");
    }

    @Test
    void execute_studentExceededRequirement_stillShowsCompleted() {
        // Arrange - student with 50 modules (200 MCs)
        Student student = new Student.Builder("Grace Teo")
                .course("CS")
                .build();

        for (int i = 0; i < 50; i++) {
            student.addModule(new Module("CS" + (5000 + i), Grade.B_PLUS));
        }

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("200/160 MCs"), "Should show 200 MCs");
        assertTrue(message.contains("Status: Completed"),
                "Status should still be 'Completed' for over 160 MCs");
    }

    @Test
    void execute_multipleStudents_secondStudent() {
        // Arrange
        Student student1 = new Student.Builder("Student One").course("CS").build();
        Student student2 = new Student.Builder("Student Two").course("CEG").build();
        student2.addModule(new Module("CS2103T", Grade.A));

        db.addStudent(student1);
        db.addStudent(student2);

        StatusCommand cmd = new StatusCommand(2);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("Index 2:"), "Should show index 2");
        assertTrue(message.contains("Student Two"), "Should show second student name");
        assertTrue(message.contains("CEG"), "Should show CEG course");
        assertTrue(message.contains("4/160 MCs"), "Should show 4 MCs");
    }

    @Test
    void execute_studentWithMixedGrades_correctCapCalculation() {
        // Arrange
        Student student = new Student.Builder("Harry Tan")
                .course("CS")
                .build();

        student.addModule(new Module("CS2103T", Grade.A_PLUS)); // 5.0
        student.addModule(new Module("CS2101", Grade.B));       // 3.5
        student.addModule(new Module("CS2100", Grade.C));       // 2.5
        student.addModule(new Module("CS2105", Grade.F));       // 0.0

        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        double expectedCap = (5.0 + 3.5 + 2.5 + 0.0) / 4.0; // 2.75
        String message = result.getMessage();
        assertTrue(message.contains(String.format("Cap %.1f", expectedCap)),
                "CAP should correctly handle mixed grades including F");
    }

    @Test
    void execute_studentWithNAFields_handlesCorrectly() {
        // Arrange
        Student student = new Student.Builder("Ian Koh").build();
        db.addStudent(student);

        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        String message = result.getMessage();
        assertTrue(message.contains("Ian Koh"), "Should contain student name");
        assertTrue(message.contains("N.A."), "Should show N.A. for missing fields");
    }

    @Test
    void execute_indexZero_throwsException() {
        // Arrange
        db.addStudent(new Student.Builder("Test Student").build());
        StatusCommand cmd = new StatusCommand(0);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.execute(db, storage),
                "Should throw exception for index 0");

        assertEquals("Invalid index: 0", exception.getMessage(),
                "Exception message should indicate invalid index");
    }

    @Test
    void execute_negativeIndex_throwsException() {
        // Arrange
        db.addStudent(new Student.Builder("Test Student").build());
        StatusCommand cmd = new StatusCommand(-5);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.execute(db, storage),
                "Should throw exception for negative index");

        assertEquals("Invalid index: -5", exception.getMessage());
    }

    @Test
    void execute_indexTooLarge_throwsException() {
        // Arrange
        db.addStudent(new Student.Builder("Test Student").build());
        StatusCommand cmd = new StatusCommand(2);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.execute(db, storage),
                "Should throw exception for index beyond database size");

        assertEquals("Invalid index: 2", exception.getMessage());
    }

    @Test
    void execute_emptyDatabase_throwsException() {
        // Arrange
        StatusCommand cmd = new StatusCommand(1);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.execute(db, storage),
                "Should throw exception for empty database");

        assertTrue(exception.getMessage().contains("Invalid index"),
                "Exception should indicate invalid index");
    }

    @Test
    void execute_withoutStorage_returnsNull() {
        // Arrange
        db.addStudent(new Student.Builder("Test Student").build());
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db);

        // Assert
        assertNull(result, "execute without storage should return null");
    }

    @Test
    void undo_withStorage_throwsException() {
        // Arrange
        StatusCommand cmd = new StatusCommand(1);

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class,
                () -> cmd.undo(db, storage),
                "Undo should throw exception");

        assertEquals("Cannot undo status command", exception.getMessage(),
                "Exception message should indicate cannot undo");
    }

    @Test
    void undo_withoutStorage_returnsNull() {
        // Arrange
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.undo(db);

        // Assert
        assertNull(result, "undo without storage should return null");
    }

    @Test
    void isUndoable_returnsFalse() {
        // Arrange
        StatusCommand cmd = new StatusCommand(1);

        // Act & Assert
        assertFalse(cmd.isUndoable(), "StatusCommand should not be undoable");
    }

    @Test
    void execute_boundaryExactly40MCs_onTrackStatus() {
        // Arrange - exactly 40 MCs (10 modules)
        Student student = new Student.Builder("Boundary Test 40").build();
        for (int i = 0; i < 10; i++) {
            student.addModule(new Module("MOD" + i, Grade.B));
        }
        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertTrue(result.getMessage().contains("40/160 MCs"));
        assertTrue(result.getMessage().contains("Status: On Track"));
    }

    @Test
    void execute_boundaryExactly80MCs_satisfactoryStatus() {
        // Arrange - exactly 80 MCs (20 modules)
        Student student = new Student.Builder("Boundary Test 80").build();
        for (int i = 0; i < 20; i++) {
            student.addModule(new Module("MOD" + i, Grade.B));
        }
        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertTrue(result.getMessage().contains("80/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Satisfactory"));
    }

    @Test
    void execute_boundaryExactly120MCs_goodProgressStatus() {
        // Arrange - exactly 120 MCs (30 modules)
        Student student = new Student.Builder("Boundary Test 120").build();
        for (int i = 0; i < 30; i++) {
            student.addModule(new Module("MOD" + i, Grade.B));
        }
        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertTrue(result.getMessage().contains("120/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Good Progress"));
    }

    @Test
    void execute_boundary39MCs_justStartedStatus() {
        // Arrange - 39 MCs (9 modules + partial) - but since we use 4MC per module, use 9 modules = 36 MCs
        Student student = new Student.Builder("Boundary Test 36").build();
        for (int i = 0; i < 9; i++) {
            student.addModule(new Module("MOD" + i, Grade.B));
        }
        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertTrue(result.getMessage().contains("36/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Just Started"));
    }

    @Test
    void execute_boundary119MCs_satisfactoryNotGoodProgress() {
        // Arrange - 119 MCs (29 modules + partial) - use 29 modules = 116 MCs
        Student student = new Student.Builder("Boundary Test 116").build();
        for (int i = 0; i < 29; i++) {
            student.addModule(new Module("MOD" + i, Grade.B));
        }
        db.addStudent(student);
        StatusCommand cmd = new StatusCommand(1);

        // Act
        CommandResult result = cmd.execute(db, storage);

        // Assert
        assertTrue(result.getMessage().contains("116/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Satisfactory"));
    }
}
