package service;

import exceptions.ManagerSaveException;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static File file;


    @BeforeEach
    void beforeEachNew() {
        try {
            file = Files.createTempFile("testStorage", ".txt").toFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании файла");
        }
        taskManager = new FileBackedTaskManager(file);
    }

    //Проверяем создается ли новый менеджер для работы с файлами
    @Test
    void getFileTaskManager() {
        assertNotNull(taskManager, "taskManager не создался");
    }

    // проверяем добавление двух в файл, затем загрузку из файла и сравниваем загруженные таски
    @Test
    void checkSaveTasks() {
        taskManager.addTask(task);
        taskManager.addTask(task2);
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task taskToCompare = taskManager1.getTaskById(task.getId());
        Task taskToCompare2 = taskManager1.getTaskById(task2.getId());
        assertEquals(task, taskToCompare);
        assertEquals(task2, taskToCompare2);
    }

    //Использование assertThrows по ТЗ
    @Test
    public void shouldThrowExceptionForLoadFromFile() {
        ManagerSaveException exception = Assertions.assertThrows(
                ManagerSaveException.class,
                generateSave(new File("TestFile.txt"))
        );
        Assertions.assertEquals("Ошибка чтения файла", exception.getMessage());
    }

    private Executable generateSave(File file) {
        return () -> FileBackedTaskManager.loadFromFile(file);
    }


}
