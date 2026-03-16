package dextro.app;

import java.util.Scanner;
import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.exception.ParseException;
import dextro.model.record.StudentDatabase;
import dextro.parser.Parser;
import dextro.ui.Ui;

public class App {

    private final Scanner scanner;
    private final Parser parser;
    private final StudentDatabase db;

    public App(Scanner scanner, Parser parser, StudentDatabase db) {
        this.scanner = scanner;
        this.parser = parser;
        this.db = db;
    }

    public void run() {
        boolean isRunning = true;

        System.out.println("Welcome to Dextro Student Manager!");

        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine();

            try {
                Command command = parser.parse(input);
                CommandResult result = command.execute(db);
                Ui.show(result.getMessage());

                if (result.shouldExit()) {
                    isRunning = false;
                }

            } catch (ParseException e) {
                Ui.show("Error: " + e.getMessage());
            } catch (Exception e) {
                Ui.show("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
