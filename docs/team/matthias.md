# Matthias Lim's Project Portfolio Page

### Code contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=zoom&zA=ChickenPancakeBeef&zR=AY2526S2-CS2113-T11-4%2Ftp%5Bmaster%5D&zACS=221.46654040404042&zS=2026-02-20T00%3A00%3A00&zFS=&zU=2026-04-02T23%3A59%3A59&zMG=false&zFTF=commit&zFGS=groupByRepos&zFR=false)

#### Status Command Implementation
Developed the `StatusCommand` class that allows users to view comprehensive student progress information:
- Displays student's CAP (Cumulative Average Point)
- Shows total MCs (Modular Credits) completed out of 160
- Calculates and displays progress status (Just Started, On Track, Satisfactory, Good Progress, Completed)
- Handles invalid index validation with appropriate error messages
- Marked as non-undoable command

#### Undo Command Implementation
Developed the `UndoCommand` class that enables users to reverse previously executed commands:
- Integrates with `CommandHistory` to maintain stack of executed commands
- Supports undoing of create, delete, add, and remove operations
- Provides appropriate warning messages when no commands are available to undo
- Prevents undoing of non-undoable commands (status, search, sort, list, exit)
- Marked as non-undoable itself to prevent undo of undo operations

#### StatusCommand Testing
Created comprehensive `StatusCommandTest.java` with **29 rigorous test cases**:
- Positive test cases: Valid student queries, CAP calculations, module scenarios
- Negative test cases: Invalid indices (zero, negative, out of bounds), empty database
- Boundary test cases: Exact MC thresholds (40, 80, 120, 160 MCs)
- Edge cases: Students with no modules, exceeded requirements, mixed grades
- All 5 progress status levels validated

#### UndoCommand Testing
Created comprehensive `UndoCommandTest.java` with **25 rigorous test cases**:
- Positive test cases: Undo create, undo delete, multiple sequential undos
- Negative test cases: Empty history, undoing non-undoable commands
- Complex scenarios: Mixed create/delete operations, multiple students
- History management: Stack operations (push, pop, clear)
- Database integrity verification after each undo operation

#### Parser Testing for Status and Undo
Created `ParserTest.java` with **52 focused test cases** for status and undo parsing:
- Status parsing: Valid/invalid indices, whitespace handling, case insensitivity
- Undo parsing: With/without history, typos, extra arguments
- Boundary cases: MAX_VALUE, MIN_VALUE, leading zeros
- Equivalence partitioning: Valid vs invalid input classes
- Combined edge cases: Command interactions




