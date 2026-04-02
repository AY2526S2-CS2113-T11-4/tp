package dextro.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ========== STATUS COMMAND - POSITIVE TEST CASES ==========

    @Test
    void parse_statusValidIndex_success() throws ParseException {
        Command cmd = parser.parse("status 1");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should be StatusCommand");
    }

    @Test
    void parse_statusLargeIndex_success() throws ParseException {
        Command cmd = parser.parse("status 999");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should parse large index");
    }

    @Test
    void parse_statusWithWhitespace_success() throws ParseException {
        Command cmd = parser.parse("  status  5  ");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should handle whitespace");
    }

    @Test
    void parse_statusCaseInsensitive_success() throws ParseException {
        Command cmd = parser.parse("STATUS 1");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Command should be case insensitive");
    }

    @Test
    void parse_statusMixedCase_success() throws ParseException {
        Command cmd = parser.parse("StAtUs 1");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should handle mixed case");
    }

    @Test
    void parse_statusExtraWhitespace_success() throws ParseException {
        Command cmd = parser.parse("status    10");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should handle extra whitespace");
    }

    @Test
    void parse_statusMultiDigitIndex_success() throws ParseException {
        Command cmd = parser.parse("status 12345");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should parse multi-digit index");
    }

    // ========== STATUS COMMAND - NEGATIVE TEST CASES ==========

    @Test
    void parse_statusNoIndex_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status"),
                "Status without index should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"),
                "Should indicate invalid index");
    }

    @Test
    void parse_statusNonNumericIndex_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status abc"),
                "Status with non-numeric index should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"),
                "Should indicate invalid index");
    }

    @Test
    void parse_statusNegativeIndex_parsesSuccessfully() throws ParseException {
        // Note: Parser allows negative numbers, validation happens in StatusCommand.execute()
        Command cmd = parser.parse("status -5");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should parse negative index");
    }

    @Test
    void parse_statusZeroIndex_parsesSuccessfully() throws ParseException {
        // Note: Parser allows zero, validation happens in StatusCommand.execute()
        Command cmd = parser.parse("status 0");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should parse zero index");
    }

    @Test
    void parse_statusMultipleIndices_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status 1 2"),
                "Status with multiple indices should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"),
                "Should indicate invalid index format");
    }

    @Test
    void parse_statusDecimalIndex_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status 1.5"),
                "Status with decimal index should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"),
                "Should indicate invalid index");
    }

    @Test
    void parse_statusWithSpecialCharacters_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status #1"),
                "Status with special characters should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"));
    }

    @Test
    void parse_statusEmptyAfterCommand_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status   "),
                "Status with only whitespace should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"));
    }

    @Test
    void parse_statusWithLettersAndNumbers_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status 1a"),
                "Status with mixed letters and numbers should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"));
    }

    @Test
    void parse_statusWithSymbols_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("status @5"),
                "Status with symbols should throw exception");

        assertTrue(exception.getMessage().contains("Invalid index for status"));
    }

    // ========== STATUS COMMAND - BOUNDARY TEST CASES ==========

    @Test
    void parse_statusIndex1_success() throws ParseException {
        Command cmd = parser.parse("status 1");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Index 1 should be valid");
    }

    @Test
    void parse_statusMaxInteger_success() throws ParseException {
        Command cmd = parser.parse("status " + Integer.MAX_VALUE);

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Max integer should parse successfully");
    }

    @Test
    void parse_statusMinInteger_parsesSuccessfully() throws ParseException {
        // Note: Parser allows min integer, validation happens in StatusCommand.execute()
        Command cmd = parser.parse("status " + Integer.MIN_VALUE);

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Min integer should parse");
    }

    // ========== STATUS COMMAND - EQUIVALENCE PARTITION TEST CASES ==========

    @Test
    void parse_statusEquivalenceValidPositive_success() throws ParseException {
        Command cmd = parser.parse("status 5");
        assertTrue(cmd instanceof StatusCommand, "Valid positive index");
    }

    @Test
    void parse_statusEquivalenceInvalidNonNumeric_throwsException() {
        assertThrows(ParseException.class,
                () -> parser.parse("status abc"),
                "Invalid non-numeric index");
    }

    @Test
    void parse_statusEquivalenceInvalidDecimal_throwsException() {
        assertThrows(ParseException.class,
                () -> parser.parse("status 1.5"),
                "Invalid decimal index");
    }

    @Test
    void parse_statusEquivalenceInvalidEmpty_throwsException() {
        assertThrows(ParseException.class,
                () -> parser.parse("status"),
                "Invalid empty index");
    }

    // ========== UNDO COMMAND - POSITIVE TEST CASES ==========

    @Test
    void parse_undo_success() throws ParseException {
        Command cmd = parser.parse("undo");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should be UndoCommand");
    }

    @Test
    void parse_undoWithWhitespace_success() throws ParseException {
        Command cmd = parser.parse("  undo  ");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle whitespace");
    }

    @Test
    void parse_undoCaseInsensitive_success() throws ParseException {
        Command cmd = parser.parse("UNDO");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Command should be case insensitive");
    }

    @Test
    void parse_undoMixedCase_success() throws ParseException {
        Command cmd = parser.parse("UnDo");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle mixed case");
    }

    @Test
    void parse_undoLeadingWhitespace_success() throws ParseException {
        Command cmd = parser.parse("   undo");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle leading whitespace");
    }

    @Test
    void parse_undoTrailingWhitespace_success() throws ParseException {
        Command cmd = parser.parse("undo   ");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle trailing whitespace");
    }

    @Test
    void parse_undoExtraWhitespace_success() throws ParseException {
        Command cmd = parser.parse("     undo     ");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle extra whitespace");
    }

    // ========== UNDO COMMAND - NEGATIVE TEST CASES ==========

    @Test
    void parse_undoWithoutHistory_throwsException() {
        Parser parserWithoutHistory = new Parser();

        ParseException exception = assertThrows(ParseException.class,
                () -> parserWithoutHistory.parse("undo"),
                "Undo without history initialized should throw exception");

        assertTrue(exception.getMessage().contains("Command history not initialized"),
                "Should indicate history not initialized");
        assertEquals("Command history not initialized", exception.getMessage(),
                "Exception message should match exactly");
    }

    @Test
    void parse_undoWithArguments_success() throws ParseException {
        // Note: Parser ignores extra arguments after "undo" command
        Command cmd = parser.parse("undo something");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should create UndoCommand even with arguments");
    }

    @Test
    void parse_undoWithNumbers_success() throws ParseException {
        // Note: Parser ignores extra arguments after "undo" command
        Command cmd = parser.parse("undo 5");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should create UndoCommand even with numbers");
    }

    @Test
    void parse_undoTypo_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("undoo"),
                "Typo in undo should throw exception");

        assertTrue(exception.getMessage().contains("Unknown command"),
                "Should indicate unknown command");
    }

    @Test
    void parse_undoMisspelled_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("uundo"),
                "Misspelled undo should throw exception");

        assertTrue(exception.getMessage().contains("Unknown command"));
    }

    @Test
    void parse_undoSimilarCommand_throwsException() {
        ParseException exception = assertThrows(ParseException.class,
                () -> parser.parse("undo_command"),
                "Similar command should throw exception");

        assertTrue(exception.getMessage().contains("Unknown command"));
    }

    // ========== UNDO COMMAND - BOUNDARY TEST CASES ==========

    @Test
    void parse_undoMinimalInput_success() throws ParseException {
        Command cmd = parser.parse("undo");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Minimal valid input should work");
    }

    @Test
    void parse_undoWithMaxWhitespace_success() throws ParseException {
        String maxWhitespace = " ".repeat(100) + "undo" + " ".repeat(100);
        Command cmd = parser.parse(maxWhitespace);

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof UndoCommand, "Should handle excessive whitespace");
    }

    // ========== UNDO COMMAND - EQUIVALENCE PARTITION TEST CASES ==========

    @Test
    void parse_undoEquivalenceValidNoArgs_success() throws ParseException {
        Command cmd = parser.parse("undo");
        assertTrue(cmd instanceof UndoCommand, "Valid undo with no arguments");
    }

    @Test
    void parse_undoEquivalenceValidWithIgnoredArgs_success() throws ParseException {
        Command cmd = parser.parse("undo ignored");
        assertTrue(cmd instanceof UndoCommand, "Valid undo with ignored arguments");
    }

    @Test
    void parse_undoEquivalenceInvalidTypo_throwsException() {
        assertThrows(ParseException.class,
                () -> parser.parse("unddo"),
                "Invalid undo typo");
    }

    @Test
    void parse_undoEquivalenceInvalidNoHistory_throwsException() {
        Parser parserNoHistory = new Parser();
        assertThrows(ParseException.class,
                () -> parserNoHistory.parse("undo"),
                "Invalid undo without history");
    }

    // ========== COMBINED STATUS AND UNDO EDGE CASES ==========

    @Test
    void parse_statusAfterUndo_bothWorkIndependently() throws ParseException {
        Command undoCmd = parser.parse("undo");
        assertNotNull(undoCmd, "Undo command should parse");
        assertTrue(undoCmd instanceof UndoCommand);

        Command statusCmd = parser.parse("status 1");
        assertNotNull(statusCmd, "Status command should parse");
        assertTrue(statusCmd instanceof StatusCommand);
    }

    @Test
    void parse_multipleStatusCommands_eachParsedIndependently() throws ParseException {
        Command status1 = parser.parse("status 1");
        Command status2 = parser.parse("status 2");
        Command status3 = parser.parse("status 100");

        assertTrue(status1 instanceof StatusCommand);
        assertTrue(status2 instanceof StatusCommand);
        assertTrue(status3 instanceof StatusCommand);
    }

    @Test
    void parse_multipleUndoCommands_eachParsedIndependently() throws ParseException {
        Command undo1 = parser.parse("undo");
        Command undo2 = parser.parse("undo");
        Command undo3 = parser.parse("undo");

        assertTrue(undo1 instanceof UndoCommand);
        assertTrue(undo2 instanceof UndoCommand);
        assertTrue(undo3 instanceof UndoCommand);
    }

    @Test
    void parse_statusWithVeryLargeNumber_success() throws ParseException {
        Command cmd = parser.parse("status 999999999");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should handle very large numbers");
    }

    @Test
    void parse_statusWithLeadingZeros_success() throws ParseException {
        Command cmd = parser.parse("status 0001");

        assertNotNull(cmd, "Command should not be null");
        assertTrue(cmd instanceof StatusCommand, "Should handle leading zeros");
    }
}
