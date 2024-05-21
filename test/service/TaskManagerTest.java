package service;

import constants.Status;
import manager.Managers;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {


    TaskManager taskManager;
    Task task;
    Task task1;
    Task task2;
    Task task3;
    Task task4;
    Epic epic;
    Epic epic1;
    Epic epic2;
    Epic epic3;
    SubTask subTask;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    @BeforeEach
    void beforeEach() {

        taskManager = Managers.getDefault();

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
        task2 = new Task("Новая тестовая задача2",
                "Описание тестовой задачи2",
                Status.NEW,
                LocalDateTime.of(2024, 5,
                        1, 12, 0, 0),
                Duration.ofMinutes(60));
        task3 = new Task("Новая тестовая задача3",
                "Описание тестовой задачи3",
                Status.NEW,
                LocalDateTime.of(2024, 5,
                        1, 14, 0, 0),
                Duration.ofMinutes(60));
        task4 = new Task("Новая тестовая задача4",
                "Описание тестовой задачи4",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 16, 0, 0),
                Duration.ofMinutes(60));

        epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        epic1 = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        epic2 = new Epic("эпик2",
                "Описание тестового эпика2", Status.NEW);
        epic3 = new Epic("эпик3",
                "Описание тестового эпика3", Status.NEW);

        subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));
        subTask1 = new SubTask("Новая тестовая таска1",
                "Описание таски1", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 2, 12, 0, 0),
                Duration.ofMinutes(60));
        subTask2 = new SubTask("Новая тестовая таска2",
                "Описание таски2", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 5, 3, 14, 0, 0),
                Duration.ofMinutes(60));
        subTask3 = new SubTask("сабтаска для второго эпика",
                "Описание таски", Status.NEW, epic2.getId(),
                LocalDateTime.of(2024, 5, 4, 16, 0, 0),
                Duration.ofMinutes(60));

    }

    //getById методы проверяются вместе с созданием
    @Test
    void addTask() {

        taskManager.addTask(task);
        int taskId = task.getId();

        //сравниваем созданный и сохраненный таск
        Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        //Проверяем список тасок
        List<Task> tasks = taskManager.getAllTask();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //Проверяем добавление эпика
    @Test
    void addEpic() {
        //создаем и добавляем эпик

        taskManager.addEpic(epic);


        //сравниваем созданный и сохраненный эпик
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        //Проверяем список эпиков
        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    //Проверяем доавление сабтаски
    @Test
    void addSubTask() {
        //создаем и добавляем эпик

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        //сравниваем созданный и сохраненный сабтаск

        SubTask savedSubTusk = taskManager.getSubTaskById(subTask.getId());

        //Проверяем что сохраненная сабтаска не пустая
        assertNotNull(savedSubTusk, "Таска не найдена.");
        assertEquals(subTask, savedSubTusk, "Таски не совпадают.");

        //Проверяем список сабтасок
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertNotNull(subTasks, "Таски не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество тасок.");
        assertEquals(subTask, subTasks.get(0), "Таски не совпадают.");
    }

    @Test
    void changeIdCounter() {

        taskManager.addEpic(epic);

        //Создали еще один эпик

        taskManager.addEpic(epic1);


        //Проверяем их ID
        assertEquals(0, epic.getId(), "ID не соответствует ожидаемому!");
        assertEquals(1, epic1.getId(), "ID не соответствует ожидаемому!");
        assertNotEquals(epic1.getId(), epic.getId(), "ID не могут совпадать !");
    }

    //Начинаем проверят update методы
    @Test
    void updateTask() {

        taskManager.addTask(task);

        taskManager.updateTask(new Task("Обновление работы  1", "но еще поработай", task.getId(),
                Status.IN_PROGRESS));
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotEquals(task, updatedTask, "Таски доложны отличаться!");
    }

    @Test
    void updateSubTask() {

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        //Сохраняем созданный сабтаск
        SubTask oldSubTusk = subTask;

        //Создаем обновленный сабтаск
        SubTask updatedSubTask = new SubTask("ИЗМЕНЕНИЕ", "Неплохо...", Status.IN_PROGRESS, subTask.getId(),
                epic.getId(), LocalDateTime.of(2024, 5, 10, 11, 0, 0),
                Duration.ofMinutes(60));

        taskManager.updateSubTask(updatedSubTask);

        //Сравниваем оригинальный и новый сабтаск
        assertNotEquals(oldSubTusk, updatedSubTask, "Сабтаски должны различаться!");
        //ID сабтасков должны быть одинаковые!
        assertEquals(oldSubTusk.getId(), updatedSubTask.getId(), "ID должны быть одинаковые");
    }

    @Test
    void updateEpic() {
        //создаем и добавляем эпик

        taskManager.addEpic(epic);

        //Сохраняем старый эпик
        Epic oldEpic = epic;

        //Создаем новый с тем же ID
        Epic newEpic = new Epic("Новая тестовый эпик", "Описание тестового эпика", epic.getId(),
                Status.IN_PROGRESS);

        taskManager.updateEpic(newEpic);

        //Сравниваем оригинальный и новый сабтаск
        assertNotEquals(oldEpic, newEpic, "Эпики должны различаться!");
        //ID сабтасков должны быть одинаковые!
        assertEquals(oldEpic.getId(), newEpic.getId(), "ID должны быть одинаковые");
    }

    @Test
    void getAllEpics() {
        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        //получаем список эпиков и считаем его
        List<Epic> epics = taskManager.getAllEpics();
        int epicsSize = epics.size();

        //Сравниваем ожидаемый размер сиписка и фактический
        assertEquals(3, epicsSize, "Количество эпиков не совпадает");
    }

    @Test
    void getAllSubTasks() {
        //создаем и добавляем эпик

        taskManager.addEpic(epic);

        //создаем и добавляем сабтаск


        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        //Получаем список и считаем размер
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        int subTasksSize = subTasks.size();

        //Сравниваем ожидаемый размер сиписка и фактический
        assertEquals(3, subTasksSize, "Количество сабтасок не совпадает");
    }

    @Test
    void getAllTask() {
        //создаем и добавляем таски

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);

        //Получаем список и считаем размер
        List<Task> tasks = taskManager.getAllTask();
        int tasksSize = tasks.size();

        //Сравниваем ожидаемый размер сиписка и фактический
        assertEquals(4, tasksSize, "Количество тасок не совпадает");
    }

    @Test
    void delTaskById() {
        //создаем и добавляем таски

        taskManager.addTask(task1);
        taskManager.addTask(task2);


//Проверяем наличие таски
        assertNotNull(taskManager.getTaskById(task1.getId()));
//Удаляем таску
        taskManager.delTaskById(task1.getId());
//Проверяем наличие таски
        assertNull(taskManager.getTaskById(task1.getId()));
    }

    //Проверяем удаляемость эпика а так же его сабтасков вместе с ним
    @Test
    void dellEpicById() {

        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);

        //Сабтаски первого эпика
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);

        //Сабтаска второго эпика
        taskManager.addSubTask(subTask2);

        subTask2.setEpicId(epic1.getId());

        //Проверяем наличие сабтаска
        assertNotNull(taskManager.getSubTaskById(subTask.getId()));
        assertNotNull(taskManager.getSubTaskById(subTask1.getId()));
        assertNotNull(taskManager.getSubTaskById(subTask2.getId()));

        //Проверяем наличие эпика в списке
        assertNotNull(taskManager.getEpicById(epic.getId()));

        assertEquals(subTask.getEpicId(), subTask1.getEpicId(), "ID эпиков должны совпадать");

        //Удаляем эпик
        taskManager.dellEpicById(epic.getId());

        //Проверяем наличие удаленного эпика
        assertNull(taskManager.getEpicById(epic.getId()));

        //Проверяем наличие сабтасок у эпика
        assertNull(taskManager.getSubTaskById(subTask.getId()));
        assertNull(taskManager.getSubTaskById(subTask1.getId()));

        //Проверяем, что сабтаск от второго эпика остался
        assertNotNull(taskManager.getSubTaskById(subTask2.getId()));
    }

    @Test
    void dellAllEpics() {

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        //Удаляем эпики
        taskManager.dellAllEpics();
        assertNull(taskManager.getEpicById(epic1.getId()));
        assertNull(taskManager.getEpicById(epic2.getId()));
        assertNull(taskManager.getEpicById(epic3.getId()));
    }

    @Test
    void dellSubTaskById() {
        //создаем и добавляем эпики и сабтаск


        taskManager.addEpic(epic1);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        //Удаляем сабтаски
        taskManager.dellSubTaskById(subTask1.getId());
        taskManager.dellSubTaskById(subTask2.getId());

        //Проверяем их наличие
        assertNull(taskManager.getSubTaskById(subTask1.getId()));
        assertNull(taskManager.getSubTaskById(subTask2.getId()));
    }

    @Test
    void dellAllSubTasks() {
        //создаем и добавляем эпики и сабтаск

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        //удаляем сабтаски
        taskManager.dellAllSubTasks();

        //Проверяем их наличие
        assertNull(taskManager.getSubTaskById(subTask1.getId()));
        assertNull(taskManager.getSubTaskById(subTask2.getId()));
    }
}
