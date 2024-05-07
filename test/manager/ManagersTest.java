package manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import constants.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager не создался");
    }

    @Test
    void notIdenticalTasks() {
        TaskManager taskManager = Managers.getDefault();
        //создаем и добавляем таски
        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1");
        taskManager.addTask(task1);
        int idTask = task1.getId();
        //Просматриваем таску, тем самым добавляя ее в историю
        taskManager.getTaskById(idTask);

        //Проверяем наличие созданной таски в истории
        List<Task> newList = taskManager.getHistory();
        assertTrue(newList.contains(task1));

        //Обновляем таску
        Task task2 = new Task("Новая версия первой таски  1", "но еще поработай", Status.IN_PROGRESS,
                task1.getId());
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

    @Test
    void taskHistoryOrder() {
        TaskManager taskManager = Managers.getDefault();

        //создаем и добавляем таски
        Task task0 = new Task("Новая тестовая задача0", "Описание тестовой задачи0");
        taskManager.addTask(task0);
        int idTask0 = task0.getId();

        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1");
        taskManager.addTask(task1);
        int idTask1 = task1.getId();

        Task task2 = new Task("Новая тестовая задача2", "Описание тестовой задачи2");
        taskManager.addTask(task2);
        int idTask2 = task2.getId();

        Task task3 = new Task("Новая тестовая задача3", "Описание тестовой задачи3");
        taskManager.addTask(task3);
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
}