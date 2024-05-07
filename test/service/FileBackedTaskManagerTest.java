package service;

import exceptions.ManagerSaveException;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    static File file;
    Task task;
    Task task2;

    @BeforeEach
    void beforeEach() {
        try {
            file = Files.createTempFile("testStorage", ".txt").toFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании файла");
        }
        taskManager = new FileBackedTaskManager(file);
        task = new Task("Задача", "Описание задачи");
        task2 = new Task("Задача2", "Описание задачи2");
    }

    //Проверяем создается ли новый менеджер для работы с файлами
    @Test
    void getFileTaskManager() {
        assertNotNull(taskManager, "taskManager не создался");
    }

    @Test
        // проверяем добавление двух в файл, затем загрузку из файла и сравниваем загруженные таски
    void checkSaveTasks() {
        taskManager.addTask(task);
        taskManager.addTask(task2);
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        Task taskToCompare = taskManager1.getTaskById(task.getId());
        Task taskToCompare2 = taskManager1.getTaskById(task2.getId());
        assertEquals(task, taskToCompare);
        assertEquals(task2, taskToCompare2);
    }
}