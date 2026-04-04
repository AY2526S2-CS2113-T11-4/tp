package dextro.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandHistoryTest {

    private CommandHistory history;
    private Command mockCommand1;
    private Command mockCommand2;
    private Command mockCommand3;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        mockCommand1 = new CreateCommand("Alice", "91234567", "alice@test.com", "Address 1", "CS");
        mockCommand2 = new CreateCommand("Bob", "98765432", "bob@test.com", "Address 2", "CEG");
        mockCommand3 = new CreateCommand("Charlie", "87654321", "charlie@test.com", "Address 3", "ISC");
    }

    @Test
    void isEmpty_newHistory_returnsTrue() {
        assertTrue(history.isEmpty());
    }

    @Test
    void isEmpty_afterPush_returnsFalse() {
        history.push(mockCommand1);
        assertFalse(history.isEmpty());
    }

    @Test
    void push_singleCommand_commandStored() {
        history.push(mockCommand1);
        Command popped = history.pop();
        assertNotNull(popped);
        assertEquals(mockCommand1, popped);
    }

    @Test
    void push_multipleCommands_commandsStoredInLifoOrder() {
        history.push(mockCommand1);
        history.push(mockCommand2);
        history.push(mockCommand3);

        assertEquals(mockCommand3, history.pop());
        assertEquals(mockCommand2, history.pop());
        assertEquals(mockCommand1, history.pop());
    }

    @Test
    void pop_emptyHistory_returnsNull() {
        assertNull(history.pop());
    }

    @Test
    void pop_afterAllPops_historyBecomesEmpty() {
        history.push(mockCommand1);
        history.push(mockCommand2);

        history.pop();
        history.pop();

        assertTrue(history.isEmpty());
    }

    @Test
    void pop_singleCommand_historyBecomesEmpty() {
        history.push(mockCommand1);
        history.pop();
        assertTrue(history.isEmpty());
    }

    @Test
    void clear_emptyHistory_remainsEmpty() {
        history.clear();
        assertTrue(history.isEmpty());
    }

    @Test
    void clear_nonEmptyHistory_becomesEmpty() {
        history.push(mockCommand1);
        history.push(mockCommand2);
        history.push(mockCommand3);

        history.clear();

        assertTrue(history.isEmpty());
    }

    @Test
    void clear_afterClear_popReturnsNull() {
        history.push(mockCommand1);
        history.clear();
        assertNull(history.pop());
    }

    @Test
    void push_nullCommand_commandStored() {
        history.push(null);
        assertFalse(history.isEmpty());
    }

    @Test
    void pop_afterPushingNull_returnsNull() {
        history.push(null);
        assertNull(history.pop());
    }

    @Test
    void pushAndPop_alternating_maintainsCorrectState() {
        history.push(mockCommand1);
        assertEquals(mockCommand1, history.pop());
        assertTrue(history.isEmpty());

        history.push(mockCommand2);
        assertFalse(history.isEmpty());
        assertEquals(mockCommand2, history.pop());
    }

    @Test
    void push_largeNumberOfCommands_allCommandsStored() {
        int commandCount = 100;
        Command[] commands = new Command[commandCount];

        for (int i = 0; i < commandCount; i++) {
            commands[i] = new CreateCommand("Student" + i, "91234567", "test@test.com", "Addr", "CS");
            history.push(commands[i]);
        }

        for (int i = commandCount - 1; i >= 0; i--) {
            assertEquals(commands[i], history.pop());
        }

        assertTrue(history.isEmpty());
    }

    @Test
    void isEmpty_afterClearAndPush_returnsFalse() {
        history.push(mockCommand1);
        history.clear();
        history.push(mockCommand2);
        assertFalse(history.isEmpty());
    }

    @Test
    void pop_afterPushPopClearSequence_returnsNull() {
        history.push(mockCommand1);
        history.pop();
        history.clear();
        assertNull(history.pop());
        assertTrue(history.isEmpty());
    }
}
