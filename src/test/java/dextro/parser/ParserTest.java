package dextro.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import dextro.command.Command;
import dextro.command.CommandHistory;
import dextro.command.StatusCommand;
import dextro.command.UndoCommand;
import dextro.exception.ParseException;

class ParserTest {

    private Parser parser;
    private CommandHistory history;

    @BeforeEach
    void setUp() {
        parser = new Parser();
        history = new CommandHistory();
        parser.setCommandHistory(history);
    }

    // Tests for parseStatus

    @Test
    void parse_statusCommandWithValidIndex_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithIndex5_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 5");
        assertNotNull(cmd);
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithLargeIndex_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 9999");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithZero_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 0");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithNegativeNumber_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status -1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithNonNumeric_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status abc");
        });

        assertEquals("Invalid index for status: abc", exception.getMessage());
    }

    @Test
    void parse_statusWithEmptyArgument_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status ");
        });

        assertEquals("Invalid index for status: ", exception.getMessage());
    }

    @Test
    void parse_statusWithNoArgument_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status");
        });

        assertEquals("Invalid index for status: ", exception.getMessage());
    }

    @Test
    void parse_statusWithSpacesBeforeIndex_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status    3");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithDecimalNumber_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 1.5");
        });

        assertEquals("Invalid index for status: 1.5", exception.getMessage());
    }

    @Test
    void parse_statusWithExtraArguments_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 1 extra");
        });

        assertEquals("Invalid index for status: 1 extra", exception.getMessage());
    }

    @Test
    void parse_statusCaseInsensitive_returnsStatusCommand() throws ParseException {
        Command cmd1 = parser.parse("STATUS 1");
        Command cmd2 = parser.parse("Status 1");
        Command cmd3 = parser.parse("StAtUs 1");

        assertInstanceOf(StatusCommand.class, cmd1);
        assertInstanceOf(StatusCommand.class, cmd2);
        assertInstanceOf(StatusCommand.class, cmd3);
    }

    @Test
    void parse_statusWithLeadingZeros_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 001");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithMaxInteger_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status 2147483647");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithIntegerOverflow_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 9999999999999999999");
        });

        assertEquals("Invalid index for status: 9999999999999999999", exception.getMessage());
    }

    // Tests for parseUndo

    @Test
    void parse_undoCommand_returnsUndoCommand() throws ParseException {
        Command cmd = parser.parse("undo");
        assertInstanceOf(UndoCommand.class, cmd);
    }

    @Test
    void parse_undoWithExtraSpaces_returnsUndoCommand() throws ParseException {
        Command cmd = parser.parse("undo   ");
        assertInstanceOf(UndoCommand.class, cmd);
    }

    @Test
    void parse_undoCaseInsensitive_returnsUndoCommand() throws ParseException {
        Command cmd1 = parser.parse("UNDO");
        Command cmd2 = parser.parse("Undo");
        Command cmd3 = parser.parse("UnDo");

        assertInstanceOf(UndoCommand.class, cmd1);
        assertInstanceOf(UndoCommand.class, cmd2);
        assertInstanceOf(UndoCommand.class, cmd3);
    }

    @Test
    void parse_undoWithoutHistorySet_throwsParseException() {
        Parser newParser = new Parser();

        ParseException exception = assertThrows(ParseException.class, () -> {
            newParser.parse("undo");
        });

        assertEquals("Command history not initialized", exception.getMessage());
    }

    @Test
    void parse_undoWithArguments_returnsUndoCommand() throws ParseException {
        Command cmd = parser.parse("undo something");
        assertInstanceOf(UndoCommand.class, cmd);
    }

    @Test
    void parse_undoMultipleTimes_returnsNewInstanceEachTime() throws ParseException {
        Command cmd1 = parser.parse("undo");
        Command cmd2 = parser.parse("undo");

        assertInstanceOf(UndoCommand.class, cmd1);
        assertInstanceOf(UndoCommand.class, cmd2);
    }

    // General parser tests

    @Test
    void parse_emptyInput_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("");
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    void parse_blankInput_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("   ");
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    void parse_unknownCommand_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("unknown");
        });

        assertTrue(exception.getMessage().contains("Unknown command"));
    }

    @Test
    void parse_statusAfterSettingHistory_worksCorrectly() throws ParseException {
        CommandHistory newHistory = new CommandHistory();
        parser.setCommandHistory(newHistory);

        Command cmd = parser.parse("status 1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_undoAfterSettingHistory_worksCorrectly() throws ParseException {
        CommandHistory newHistory = new CommandHistory();
        parser.setCommandHistory(newHistory);

        Command cmd = parser.parse("undo");
        assertInstanceOf(UndoCommand.class, cmd);
    }

    @Test
    void parse_statusWithTabCharacters_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status\t1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithNewlineCharacters_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status\n1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_multipleConsecutiveSpacesBetweenCommandAndArgs_handlesCorrectly() throws ParseException {
        Command cmd = parser.parse("status      5");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_commandWithLeadingSpaces_handlesCorrectly() throws ParseException {
        Command cmd = parser.parse("   status 1");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_commandWithTrailingSpaces_handlesCorrectly() throws ParseException {
        Command cmd = parser.parse("status 1   ");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_mixedCaseStatusCommand_parsesCorrectly() throws ParseException {
        Command cmd = parser.parse("sTaTuS 3");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithPlusSign_returnsStatusCommand() throws ParseException {
        Command cmd = parser.parse("status +5");
        assertInstanceOf(StatusCommand.class, cmd);
    }

    @Test
    void parse_statusWithSpecialCharacters_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status #1");
        });

        assertEquals("Invalid index for status: #1", exception.getMessage());
    }

    @Test
    void parse_statusWithHexadecimalNumber_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 0x10");
        });

        assertEquals("Invalid index for status: 0x10", exception.getMessage());
    }

    @Test
    void parse_undoWithNumericArgument_stillReturnsUndoCommand() throws ParseException {
        Command cmd = parser.parse("undo 5");
        assertInstanceOf(UndoCommand.class, cmd);
    }

    @Test
    void parse_statusAfterChangingHistory_usesNewHistory() throws ParseException {
        CommandHistory oldHistory = history;
        CommandHistory newHistory = new CommandHistory();

        parser.setCommandHistory(newHistory);

        Command undoCmd = parser.parse("undo");
        assertInstanceOf(UndoCommand.class, undoCmd);
    }

    @Test
    void parse_consecutiveStatusCommands_returnsDifferentInstances() throws ParseException {
        Command cmd1 = parser.parse("status 1");
        Command cmd2 = parser.parse("status 1");

        assertInstanceOf(StatusCommand.class, cmd1);
        assertInstanceOf(StatusCommand.class, cmd2);
    }

    @Test
    void parse_statusWithVeryLongNumber_throwsParseException() {
        String longNumber = "1" + "0".repeat(1000);
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status " + longNumber);
        });

        assertTrue(exception.getMessage().contains("Invalid index for status"));
    }

    @Test
    void parse_commandWithOnlyWhitespaceAfterKeyword_handlesCorrectly() {
        assertThrows(ParseException.class, () -> {
            parser.parse("status     ");
        });
    }

    @Test
    void setCommandHistory_null_allowsSettingNull() {
        parser.setCommandHistory(null);

        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("undo");
        });

        assertEquals("Command history not initialized", exception.getMessage());
    }

    @Test
    void parse_statusWithCommaInNumber_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 1,000");
        });

        assertEquals("Invalid index for status: 1,000", exception.getMessage());
    }

    @Test
    void parse_statusWithScientificNotation_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse("status 1e5");
        });

        assertEquals("Invalid index for status: 1e5", exception.getMessage());
    }
}
