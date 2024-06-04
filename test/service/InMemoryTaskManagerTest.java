package service;

import static org.junit.jupiter.api.Assertions.*;

import constants.Status;
import exceptions.TimeCrossingException;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Test
    void epicStatusCheck() {
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);

        //Должен быть NEW
        assertEquals(epic.getStatus(), Status.NEW);


        // SubTask = IN_PROGRESS , SubTask1 = NEW
        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.IN_PROGRESS,
                subTask.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));


        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        // SubTask = DONE , SubTask1 = NEW
        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.DONE,
                subTask.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        //SubTask = IN_PROGRESS , SubTask1 = IN_PROGRESS
        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.IN_PROGRESS,
                subTask.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));

        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.IN_PROGRESS,
                subTask1.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());


//SubTask = DONE , SubTask1 = DONE
        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.DONE,
                subTask.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));
        taskManager.updateSubTask(new SubTask("Обновленная тестовая таска", "Описание таски",
                Status.DONE,
                subTask1.getId(),
                epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60)));
        assertEquals(Status.DONE, epic.getStatus());

    }

    @Test
    void timeCrossing() {
        task = new Task("Новая тестовая задача",
                "Описание тестовой задачи",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 8, 0, 0),
                Duration.ofMinutes(60));

        task1 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 10, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addTask(task);
        taskManager.addTask(task1);


        List<Task> tasks = taskManager.getAllTask();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        //Создаем задачу которая совпадает временем начала и окончаня с task

        task2 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 10, 0, 0),
                Duration.ofMinutes(60));

        try {
            taskManager.addTask(task2);
        } catch (TimeCrossingException e) {

            //Количество задач не должно увеличиться
            tasks = taskManager.getAllTask();
            assertEquals(2, tasks.size(), "Неверное количество задач.");
        }

        //Время начала и окончания task3 между началом и концом task
        task3 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 10, 30, 0),
                Duration.ofMinutes(60));
        try {
            taskManager.addTask(task3);
        } catch (TimeCrossingException e) {
            tasks = taskManager.getAllTask();
            assertEquals(2, tasks.size(), "Неверное количество задач.");
        }

        //Время окончания task и начала  task4 совпадают, доджно добавиться
        task4 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 11, 00, 0),
                Duration.ofMinutes(60));
        taskManager.addTask(task4);

        tasks = taskManager.getAllTask();
        assertEquals(3, tasks.size(), "Неверное количество задач.");


        //Пытаемся добавить сабтаск со временем начала как у  task4
        taskManager.addEpic(epic);

        subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 11, 0, 0),
                Duration.ofMinutes(60));
        try {
            taskManager.addTask(subTask);
        } catch (TimeCrossingException e) {
            tasks = taskManager.getAllTask();
            assertEquals(3, tasks.size(), "Неверное количество задач.");
        }
    }


}