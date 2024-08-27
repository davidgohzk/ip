package barney;

import java.util.Scanner;

import barney.action.CommandManager;
import barney.action.commands.Command;
import barney.data.TaskList;
import barney.data.exception.BarneyException;
import barney.storage.Storage;
import barney.ui.Ui;

public class Barney {

    private Scanner scanner;
    private CommandManager commandManager;
    private Ui ui;
    private Storage storage;
    private TaskList tasks;

    public Barney(String filePath) {
        scanner = new Scanner(System.in);
        commandManager = new CommandManager();
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.loadData());
            ui.printLoadData(tasks);
        } catch (BarneyException e) {
            ui.printLoadingError(e.getMessage());
            tasks = new TaskList();
        }

    }

    public static void main(String[] args) {
        String saveFilePath = "list.txt";
        if (args.length > 0) {
            saveFilePath = args[0];
        }
        new Barney(saveFilePath).run();
    }

    public void run() {
        ui.printWelcome();

        boolean isChatting = true;
        while (isChatting) {
            ui.printInput();
            try {
                String line = scanner.nextLine();
                if (line.matches("^\\s*$")) {
                    continue;
                }
                Command command = commandManager.getCommand(line);
                isChatting = command.execute(tasks, ui);
            } catch (BarneyException e) {
                ui.printChatError(e.getMessage());
            } catch (Exception e) {
                ui.printChatError("An unknown error occurred. Please try again. " + e.getMessage());
            }

            try {
                storage.writeData(tasks);
            } catch (BarneyException e) {
                ui.printSaveError(e.getMessage());
            }
        }
        ui.printBye();
        scanner.close();
    }
}
