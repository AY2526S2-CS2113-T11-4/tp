# Chong Kai Jie - Project Portfolio Page

## Overview

Dextro is a desktop application for managing student academic records, optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI). It helps educators and administrators track student progress, manage course modules, and monitor academic performance efficiently.

## Summary of Contributions

### Code contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=ffluryy&tabRepo=AY2526S2-CS2113-T11-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

Classes written:
```
App
AddCommand
RemoveCommand
Command
CommandResult
CreateCommand
ListCommand
Config
CommandException
ParseException
StudentDatabase
Grade
Module
Student
ArgumentTokenizer
Parser
Ui
Main
```

### Enhancements implemented

#### New Feature: Create
A way for users to add a new student record by inputting name and other optional fields.

#### New Feature: List
A way to view the entire database, or ranges of id numbers

#### New Feature: Add
A way to add a module/grade to a student's data

#### New Feature: Remove
A way to remove a module that matches the given code in a student's data

#### Enhancement to ArgumentTokenizer
Improved robustness against badly formed inputs by throwing an exception when 

#### Enhancement to Edit


### Contributions to the User Guide
- Wrote the descriptions and examples to the following functions: ```create, delete, edit, list, find, search, status, undo, sort, exit, add, remove```
- Wrote Command Format Notes
- Added some examples to Command Summary

### Contributions to the Developer Guide
- Design: 
  - Main
  - Config
  - Exceptions: CommandException/ParseException
  - Model: Student/StudentDatabase, Grade/Module
  - Ui
  - Parser/ArgumentTokenizer
  - Storage
  - Command
- Implementation:
  - AddCommand
  - RemoveCommand
  - CreateCommand
  - DeleteCommand
  - ListCommand

Created the write-ups and diagrams for the above listed items.

### Contributions to team-based tasks
- Caught bugs for other team members
- Reminded team members on deadlines 
- Suggested new features
- Approve PRs
- Released v2.0.1
- Sorted and assigned all the bugs from PE-D to the respective dev
