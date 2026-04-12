package dextro.command;

import java.util.Stack;

public class CommandHistory {
    private final Stack<Command> history = new Stack<>();

    public void push(Command command) {
        assert command != null : "Command should not be null when pushing to history";
        assert command.isUndoable() : "Only undoable commands should be pushed to history";
        history.push(command);
    }

    public Command pop() {
        assert history != null : "History stack should not be null";
        Command command = history.isEmpty() ? null : history.pop();
        assert command == null || command.isUndoable() : "Popped command should be undoable or null";
        return command;
    }

    public boolean isEmpty() {
        assert history != null : "History stack should not be null";
        return history.isEmpty();
    }

    public void clear() {
        assert history != null : "History stack should not be null";
        history.clear();
        assert history.isEmpty() : "History should be empty after clear";
    }
}
