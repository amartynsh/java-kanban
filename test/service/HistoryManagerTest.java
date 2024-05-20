package service;

import constants.Status;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {

    @Test
    void emptyHistory() {
        TaskManager taskManager = Managers.getDefault();
        List<Task> historyList = taskManager.getHistory();
        //История пуста
        assertEquals(0, historyList.size());

        Task task3 = new Task("Новая тестовая задача2",
                "Описание тестовой задачи2",
                Status.NEW,
                LocalDateTime.of(2024, 5, 13, 10, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addTask(task3);
        taskManager.getTaskById(task3.getId());

        //История не пуста
        historyList = taskManager.getHistory();
        assertEquals(1, historyList.size());
    }

    @Test
    void taskHistoryOrder() {
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем таски
        Task task0 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));

        Task task1 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 11, 10, 0, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("Новая тестовая задача2",
                "Описание тестовой задачи2",
                Status.NEW,
                LocalDateTime.of(2024, 5, 12, 10, 0, 0),
                Duration.ofMinutes(60));

        Task task3 = new Task("Новая тестовая задача2",
                "Описание тестовой задачи2",
                Status.NEW,
                LocalDateTime.of(2024, 5, 13, 10, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addTask(task0);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        int idTask0 = task0.getId();
        int idTask1 = task1.getId();
        int idTask2 = task2.getId();
        int idTask3 = task3.getId();

        //Просматриваем таски, тем самым добавляя их в историю просмотра
        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask0);
        taskManager.getTaskById(idTask3);
        taskManager.getTaskById(idTask3);

        //Должны запомниться только эти таски
        taskManager.getTaskById(idTask3);
        taskManager.getTaskById(idTask0);
        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask2);

        //Создаем правильный порядок ввода
        List<Task> newList = taskManager.getHistory();

        //Создаем правильный порядок задач, с которым будем сравнивать
        List<Integer> correctOrderHistory = new ArrayList<>();

        correctOrderHistory.add(3);
        correctOrderHistory.add(0);
        correctOrderHistory.add(1);
        correctOrderHistory.add(2);

        //Список для выгрузки истории ID задач
        List<Integer> currentOrderHistory = new ArrayList<>();
        //Выгружаем историю задач в список по ID
        for (Task task : newList) {
            currentOrderHistory.add(task.getId());
        }
        //Сравниваем ожидаемый порядок с текущим
        assertEquals(correctOrderHistory, currentOrderHistory);
    }

    @Test
    void notIdenticalTasks() {
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем таски

        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        taskManager.addTask(task1);
        int idTask = task1.getId();
        //Просматриваем таску, тем самым добавляя ее в историю
        taskManager.getTaskById(idTask);

        //Проверяем наличие созданной таски в истории
        List<Task> newList = taskManager.getHistory();
        assertTrue(newList.contains(task1));

        //Обновляем таску
        Task task2 = new Task("Обновил", "Обновил",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        taskManager.updateTask(task2);

        //Обновляем список
        newList = taskManager.getHistory();

        //Запрашиваем новую таску
        assertFalse(newList.contains(task2));

        //Обновляем историю
        taskManager.getTaskById(task2.getId());

        //Обновляем список
        newList = taskManager.getHistory();

        //Запрашиваем новую таску
        assertTrue(newList.contains(task2));
    }
    //Проверяем порядок задач в списке

}
