package barney.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import barney.data.TaskList;
import barney.data.exception.BarneyException;
import barney.data.exception.InvalidSaveFormatException;
import barney.data.task.DeadlineTask;
import barney.data.task.EventTask;
import barney.data.task.Task;
import barney.data.task.TodoTask;

/**
 * The Storage class is responsible for reading and writing data to a file. It
 * provides methods to load data from a file and write data to a file. The file
 * path is specified during the instantiation of the Storage object.
 * 
 * The format of the file should follow the specified format: Each line
 * represents a task, and each task is separated by the SAVE_FILE_DELIMITER. The
 * task data is stored in the following order: - Task status (0 for unmarked, 1
 * for marked) - Task description - Task type (T for TodoTask, D for
 * DeadlineTask, E for EventTask) - Additional data based on the task type: -
 * For DeadlineTask: Deadline date - For EventTask: Event date and time
 * 
 * The Storage class provides the following public methods: - loadData(): Loads
 * data from the file and returns an ArrayList of Task objects. -
 * writeData(TaskList taskList): Writes the given list of tasks to the file.
 * 
 * The Storage class also provides private helper methods: - readFile(): Reads
 * the file and returns an ArrayList of Task objects. -
 * writeFile(ArrayList<Task> taskList): Writes the given list of tasks to the
 * file.
 * 
 * The Storage class throws the following exceptions: - FileNotFoundException:
 * If the file specified by the file path does not exist. -
 * InvalidSaveFormatException: If the file has an invalid format or contains
 * invalid data. - IOException: If an I/O error occurs while reading or writing
 * the file. - BarneyException: If there is an error loading or writing the
 * file.
 */
public class Storage {

    private static final String SAVE_FILE_DELIMITER = "###";
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads a file and returns an ArrayList of Task objects.
     * 
     * @return The ArrayList of Task objects read from the file.
     * @throws FileNotFoundException      If the file specified by the filePath does
     *                                    not exist.
     * @throws InvalidSaveFormatException If the file has an invalid format or
     *                                    contains invalid data.
     */
    private ArrayList<Task> readFile() throws FileNotFoundException, InvalidSaveFormatException {
        ArrayList<Task> taskList = new ArrayList<Task>();
        File listFile = new File(filePath);
        Scanner fileScanner = new Scanner(listFile);
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] taskData = line.split(SAVE_FILE_DELIMITER);

            Task newTask;

            // description
            String description = taskData[1];

            // taskType
            String taskType = taskData[2];
            switch (taskType) {
            case "T":
                newTask = new TodoTask(description);
                break;
            case "D":
                newTask = new DeadlineTask(description, taskData[3]);
                break;
            case "E":
                newTask = new EventTask(description, taskData[3], taskData[4]);
                break;
            default:
                throw new InvalidSaveFormatException("Invalid task type in the file: " + taskData[2]);
            }

            // isMarked
            switch (taskData[0]) {
            case "1":
                newTask.mark();
                break;
            case "0":
                newTask.unmark();
                break;
            default:
                throw new InvalidSaveFormatException("Invalid task status in the file: " + taskData[1]);
            }

            taskList.add(newTask);
        }
        fileScanner.close();
        return taskList;
    }

    /**
     * Loads data from a file.
     *
     * @return An ArrayList of Task objects containing the loaded data.
     * @throws BarneyException If the file is not found or there is an error reading
     *                         the file.
     */
    public ArrayList<Task> loadData() throws BarneyException {
        try {
            return readFile();
        } catch (FileNotFoundException e) {
            throw new BarneyException("File not found: " + e.getMessage());
        } catch (Exception e) {
            throw new BarneyException("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Writes the given list of tasks to a file.
     * 
     * @param taskList the list of tasks to be written
     * @throws FileNotFoundException if the file specified by the file path cannot
     *                               be found
     * @throws IOException           if an I/O error occurs while writing to the
     *                               file
     */
    private void writeFile(ArrayList<Task> taskList) throws FileNotFoundException, IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        for (Task task : taskList) {
            for (String data : task.toSaveArray()) {
                fileWriter.write(data + SAVE_FILE_DELIMITER);
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    /**
     * Writes the data from the given TaskList to a file.
     *
     * @param taskList the TaskList containing the data to be written
     * @throws BarneyException if there is an error writing the file
     */
    public void writeData(TaskList taskList) throws BarneyException {
        try {
            writeFile(taskList.getArrayList());
        } catch (FileNotFoundException e) {
            throw new BarneyException("File not found: " + e.getMessage());
        } catch (IOException e) {
            throw new BarneyException("Error writing file: " + e.getMessage());
        } catch (Exception e) {
            throw new BarneyException(e.getMessage());
        }
    }
}
