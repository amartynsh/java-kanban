package manager;

import service.*;

import java.io.*;

public class Managers {
    private static File file = new File("taskStorage.txt");

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}
