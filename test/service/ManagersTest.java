package service;

import java.util.List;
import constants.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultHistory() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager не создался");
    }

    @Test
    void notIdenticalTasks() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Проверяем успешность создания истории
        assertNotNull(historyManager, "historyManager не создался");
        //создаем и добавляем таски
        Task task1 = new Task("Новая тестовая задача1", "Описание тестовой задачи1");
        taskManager.addTask(task1);
        //Просматриваем таску, тем самым добавляя ее в историю
        taskManager.getTaskById(task1.getId());

        //Проверяем наличие созданной таски в истории
        List<Task> newList = historyManager.getDefaultHistory();
        assertTrue(newList.contains(task1));

        //Обновляем таску
        Task task2 = new Task("Новая версия первой таски  1", "но еще поработай", Status.IN_PROGRESS,
                task1.getId());
        taskManager.updateTask(task2);

        //Обновляем список
        newList = historyManager.getDefaultHistory();

        //Запрашиваем новую таску
        assertFalse(newList.contains(task2));

        //Обновляем историю
        taskManager.getTaskById(task2.getId());

        //Обновляем список
        newList = historyManager.getDefaultHistory();

        //Запрашиваем новую таску
        assertTrue(newList.contains(task2));
    }
}