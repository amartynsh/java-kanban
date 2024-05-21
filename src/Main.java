import model.Epic;
import constants.Status;
import model.SubTask;
import model.Task;
import manager.Managers;
import service.TaskManager;
import exceptions.ManagerSaveException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Доработать работу 1",
                "Работа плохо проработана  поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 13, 0, 0),
                Duration.ofMinutes(60));

        Task task2 = new Task("Переработать работу 2",
                "Неплохо  неплохо... но еще поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 11, 0, 1),
                Duration.ofMinutes(60));
        Task task3 = new Task("Переработать работу 3",
                "Неплохо  неплохо... но еще поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 11, 0, 1),
                Duration.ofMinutes(60));

        Task task4 = new Task("Переработать работу 4",
                "Неплохо  неплохо... но еще поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 22, 0, 1),
                Duration.ofMinutes(60));
        Task task5 = new Task("Переработать работу 5",
                "Неплохо  неплохо... но еще поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 14, 0, 1),
                Duration.ofMinutes(60));
        Task task6 = new Task("Переработать работу 6",
                "Неплохо  неплохо... но еще поработай",
                Status.NEW,
                LocalDateTime.of(2024, 5, 10, 7, 0, 1),
                Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task6);


        System.out.println("Распечатываем список всех тасок: " + "\n" + taskManager.getAllTask());
        System.out.println();
        System.out.println();

        //Изменяем первый таск
        taskManager.updateTask(new Task("Обновление работы  1",
                "но еще поработай",
                task1.getId(),
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60)));

        //Проверяем
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println();

//создаем два эпика
        Epic epic1 = new Epic("Очень большая работа 1",
                "Эта работа делится на 2 подзадачи",
                Status.NEW);

        Epic epic2 = new Epic("Не очень большая работа 2",
                "Эту работу можно поделить на 1 подзадачу",
                Status.NEW);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        System.out.println("Эпик с пустым старттаймом: " + taskManager.getAllEpics());

//Создаем сабтаски для первого эпика
//String name, String description, Status status,int id, int epicId, LocalDateTime startTime, Duration duration)
        SubTask subTask1 = new SubTask("Сабтаск1 на эпик 1",
                "Работа плохо проработана  поработай",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 5, 1, 12, 0, 0),
                Duration.ofMinutes(60));

        SubTask subTask2 = new SubTask("Сабтаск2 на эпик 1",
                "Неплохо, неплохо... но еще поработай",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 5, 13, 10, 0, 0),
                Duration.ofMinutes(60));

        SubTask subTask11 = new SubTask("Сабтаск11 на эпик 1",
                "Неплохо, неплохо... но еще поработай",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 5, 14, 10, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addSubTask(subTask11);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        System.out.println();
        System.out.println();

        System.out.println(taskManager.getAllSubTasks());

        System.out.println("Проверяем что дата старта эпика - по самому раннему сабтаску: "
                + taskManager.getEpicById(epic1.getId()).getStartTime().isEqual(subTask1.getStartTime()));

        System.out.println();
        System.out.println();

        System.out.println("ПЕЧАТАЕМ ИСТОРИЮ ПРОСМОТРА " + taskManager.getHistory());
        System.out.println();
        System.out.println();


        // Создаем сабтаск для второго эпика
        SubTask subTask3 = new SubTask("Сабтаск1 на эпик 2",
                "Нужно очень многоработать",
                Status.NEW, epic2.getId(),
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        taskManager.addSubTask(subTask3);
        SubTask subTask10 = new SubTask("Сабтаск2 на эпик 2", "Нужно очень многоработать",
                Status.NEW, epic2.getId(),
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        taskManager.addSubTask(subTask10);

        //Печатаем список сабтасков первого эпика
        System.out.println("смотри список сабтасков у эпика 1" + taskManager.getEpicSubtasks(epic1.getId()));
        System.out.println();
        System.out.println();


        // Меняем состояние первого сабтаска первого эпика на IN_PROGRESS
        SubTask subTask4 = new SubTask("ИЗМЕНЕНИЕ", "Неплохо...",
                Status.IN_PROGRESS, epic1.getId(),
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        taskManager.updateSubTask(subTask4);

        System.out.println("Cмотрим НВОЫЙ статус у эпика 1 (должен быть IN_PROGRESS) = " + epic1.getStatus());
        System.out.println();
        System.out.println();

        System.out.println("СПИСОК ВСЕХ ЭПИКОВ" + taskManager.getAllEpics());
        System.out.println();
        System.out.println();

        //Запрашиваем по ID для того, что бы наполнить историю просмотров
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());


        //Изменяем все сабтаски первого эпика на DONE
        SubTask subTask5 = new SubTask("ИЗМЕНЕНИЕ",
                "Неплохо...", Status.DONE, subTask1.getId(),
                epic1.getId(),
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        SubTask subTask6 = new SubTask("ИЗМЕНЕНИЕ2",
                "...", Status.DONE, subTask2.getId(),
                epic1.getId(),
                LocalDateTime.of(2024, 5, 10, 10, 0, 0),
                Duration.ofMinutes(60));

        taskManager.updateSubTask(subTask5);
        taskManager.updateSubTask(subTask6);
        System.out.println("смотрим НВОЫЙ список сабтасков у эпика 1" + taskManager.getEpicSubtasks(epic1.getId()));
        System.out.println("смотрим НВОЫЙ статус у эпика 1 = " + epic1.getStatus());
        //удаляем ТАСК 1
        taskManager.delTaskById(task1.getId());

        System.out.println("провекра 2 эпика перед удалением: " + taskManager.getEpicSubtasks(epic2.getId()));

        //Удаляем EPIC 2
        taskManager.dellEpicById(epic2.getId());

        //Удаляем сабтаск эпика 2
        taskManager.dellSubTaskById(subTask6.getId());
        taskManager.delTaskById(2);
        System.out.println("ПЕЧАТАЕМ ИСТОРИЮ ПРОСМОТРА " + taskManager.getHistory());
    }
}
