package dextro;

import dextro.app.App;
import dextro.parser.Parser;
import dextro.model.record.StudentDatabase;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        StudentDatabase db = new StudentDatabase();

        App app = new App(scanner, parser, db);
        app.run();

        scanner.close();
    }
}
