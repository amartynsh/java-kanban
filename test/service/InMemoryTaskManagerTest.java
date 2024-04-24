package service;

import constants.Status;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    //getById методы проверяются вместе с созданием
    @Test
    void addTask() {
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпик
        Task task = new Task("Новая тестовая задача", "Описание тестовой задачи");
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
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        //сравниваем созданный и сохраненный эпик
        Epic savedEpic = taskManager.getEpicById(epicId);
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
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);

        //создаем и добавляем сабтаск
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Новая тестовая таска", "Описание таски)", epicId);
        taskManager.addSubTask(subTask);

        //сравниваем созданный и сохраненный сабтаск
        int subTaskId = subTask.getId();
        SubTask savedSubTusk = taskManager.getSubTaskById(subTaskId);

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
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //Создали эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        //Создали еще один эпик
        Epic epic1 = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic1);
        int epic1Id = epic1.getId();

        //Проверяем их ID
        assertEquals(0, epicId, "ID не соответствует ожидаемому!");
        assertEquals(1, epic1Id, "ID не соответствует ожидаемому!");
        assertNotEquals(epic1Id, epicId, "ID не могут совпадать !");
    }

    //Начинаем проверят update методы
    @Test
    void updateTask() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Новая тестовая задача", "Описание тестовой задачи");
        taskManager.addTask(task);
        int taskId = task.getId();

        //Сохраняем созданный таск и сравниваем его с обновленным
        Task savedTask = taskManager.getTaskById(taskId);

        taskManager.updateTask(new Task("Обновление работы  1", "но еще поработай",
                Status.IN_PROGRESS, task.getId()));
        Task updatedTask = taskManager.getTaskById(taskId);
        assertNotEquals(task, updatedTask, "Таски доложны отличаться!");
    }

    @Test
    void updateSubTask() {
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);

        //создаем и добавляем сабтаск
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Новая тестовая таска", "Описание таски)", epicId);
        taskManager.addSubTask(subTask);

        //Сохраняем созданный сабтаск
        int subTaskId = subTask.getId();
        SubTask oldSubTusk = subTask;

        //Создаем обновленный сабтаск
        SubTask updatedSubTask = new SubTask("ИЗМЕНЕНИЕ", "Неплохо...", Status.IN_PROGRESS, subTaskId,
                epicId);

        taskManager.updateSubTask(updatedSubTask);

        //Сравниваем оригинальный и новый сабтаск
        assertNotEquals(oldSubTusk, updatedSubTask, "Сабтаски должны различаться!");
        //ID сабтасков должны быть одинаковые!
        assertEquals(oldSubTusk.getId(), updatedSubTask.getId(), "ID должны быть одинаковые");
    }

    @Test
    void updateEpic() {
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        //Сохраняем старый эпик
        Epic oldEpic = epic;

        //Создаем новый с тем же ID
        Epic newEpic = new Epic("Новая тестовый эпик", "Описание тестового эпика",
                Status.IN_PROGRESS, epicId);

        taskManager.updateEpic(newEpic);

        //Сравниваем оригинальный и новый сабтаск
        assertNotEquals(oldEpic, newEpic, "Эпики должны различаться!");
        //ID сабтасков должны быть одинаковые!
        assertEquals(oldEpic.getId(), newEpic.getId(), "ID должны быть одинаковые");
    }

    @Test
    void getAllEpics() {
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпики
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("эпик1", "Описание тестового эпика1");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("эпик2", "Описание тестового эпика2");
        taskManager.addEpic(epic2);

        //получаем список эпиков и считаем его
        List<Epic> epics = taskManager.getAllEpics();
        int epicsSize = epics.size();

        //Сравниваем ожидаемый размер сиписка и фактический
        assertEquals(3, epicsSize, "Количество эпиков не совпадает");
    }

    @Test
    void getAllSubTasks() {
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем эпик
        Epic epic = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);

        //создаем и добавляем сабтаск
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Новая тестовая таска", "Описание таски", epicId);
        SubTask subTask1 = new SubTask("Новая тестовая таска1", "Описание таски1", epicId);
        SubTask subTask2 = new SubTask("Новая тестовая таска2", "Описание таски2", epicId);
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
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем таски
        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1");
        Task task2 = new Task("Новая тестовая задача2", "Описание тестовой задачи2");
        Task task3 = new Task("Новая тестовая задача3", "Описание тестовой задачи3");
        Task task4 = new Task("Новая тестовая задача4", "Описание тестовой задачи4");
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
        //Создаем таскменеджер
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем таски
        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1");
        Task task2 = new Task("Новая тестовая задача2", "Описание тестовой задачи2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        int task1Id = task1.getId();

//Проверяем наличие таски
        assertNotNull(taskManager.getTaskById(task1Id));
//Удаляем таску
        taskManager.delTaskById(task1Id);
//Проверяем наличие таски
        assertNull(taskManager.getTaskById(task1Id));
    }

    //Проверяем удаляемость эпика а так же его сабтасков вместе с ним
    @Test
    void dellEpicById() {
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпики и сабтаск
        Epic epic1 = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        Epic epic2 = new Epic("эпик1", "Описание тестового эпика1");
        Epic epic3 = new Epic("эпик2", "Описание тестового эпика2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        int epicId1 = epic1.getId();
        int epicId2 = epic2.getId();
        SubTask subTask1 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
        SubTask subTask2 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        //Сабтаска для другого эпика для проверки
        SubTask subTask3 = new SubTask("сабтаска для второго эпика", "Описание таски", epicId2);
        taskManager.addSubTask(subTask3);

        int subTaskId1 = subTask1.getId();
        int subTaskId2 = subTask2.getId();
        int subTaskId3 = subTask3.getId();

        //Проверяем наличие сабтаска
        assertNotNull(taskManager.getSubTaskById(subTaskId1));
        assertNotNull(taskManager.getSubTaskById(subTaskId2));

        //Проверяем наличие эпика в списке
        assertNotNull(taskManager.getEpicById(epicId1));

        assertEquals(subTask1.getEpicId(), subTask2.getEpicId(), "ID эпиков должны совпадать");

        //Удаляем эпик
        taskManager.dellEpicById(0);

        //Проверяем наличие удаленного эпика
        assertNull(taskManager.getEpicById(epicId1));

        //Проверяем наличие сабтасок у эпика
        assertNull(taskManager.getSubTaskById(subTaskId1));
        assertNull(taskManager.getSubTaskById(subTaskId2));

        //Проверяем, что сабтаск от второго эпика остался
        assertNotNull(taskManager.getSubTaskById(subTaskId3));
    }

    @Test
    void dellAllEpics() {
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпики и сабтаск
        Epic epic1 = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        Epic epic2 = new Epic("эпик1", "Описание тестового эпика1");
        Epic epic3 = new Epic("эпик2", "Описание тестового эпика2");
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
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем эпики и сабтаск
        Epic epic1 = new Epic("Новая тестовый эпик", "Описание тестового эпика");

        taskManager.addEpic(epic1);

        int epicId1 = epic1.getId();
        SubTask subTask1 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
        SubTask subTask2 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
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
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем эпики и сабтаск
        Epic epic1 = new Epic("Новая тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic1);

        int epicId1 = epic1.getId();
        SubTask subTask1 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
        SubTask subTask2 = new SubTask("Новая тестовая таска", "Описание таски", epicId1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        //удаляем сабтаски
        taskManager.dellAllSubTasks();

        //Проверяем их наличие
        assertNull(taskManager.getSubTaskById(subTask1.getId()));
        assertNull(taskManager.getSubTaskById(subTask2.getId()));
    }
}