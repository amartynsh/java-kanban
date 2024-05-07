import model.Epic;
import constants.Status;
import model.SubTask;
import model.Task;
import manager.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        //Печатаем пустую историю просмотра
        System.out.println("ПЕЧАТАЕМ ИСТОРИЮ ПРОСМОТРА " + taskManager.getHistory());

        Task task1 = new Task("Доработать работу 1", "Работа плохо проработана  поработай");
        Task task2 = new Task("Переработать работу 2", "Неплохо  неплохо... но еще поработай");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // распечатаем список тасков
        System.out.println("ПЕЧАТАЕМ СПИСОК ТАСОК " + taskManager.getAllTask());
//Изменяем первый таск
        taskManager.updateTask(new Task("Обновление работы  1", "но еще поработай",
                Status.IN_PROGRESS, task1.getId()));
        //Проверяем
        System.out.println(taskManager.getAllTask());
//создаем два эпика
        Epic epic1 = new Epic("Очень большая работа 1", "Эта работа делится на 2 подзадачи");
        Epic epic2 = new Epic("Не очень большая работа 2", "Эту работу можно поделить на 1 подзадачу");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

//Создаем сабтаски для первого эпика
        SubTask subTask1 = new SubTask("Сабтаск1 на эпик 1", "Работа плохо проработана  поработай",
                epic1.getId());
        SubTask subTask2 = new SubTask("Сабтаск2 на эпик 1", "Неплохо, неплохо... но еще поработай",
                epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        System.out.println("ПЕЧАТАЕМ ИСТОРИЮ ПРОСМОТРА " + taskManager.getHistory());
        // Создаем сабтаск для второго эпика
        SubTask subTask3 = new SubTask("Сабтаск1 на эпик 2", "Нужно очень многоработать",
                epic2.getId());
        taskManager.addSubTask(subTask3);
        SubTask subTask10 = new SubTask("Сабтаск2 на эпик 2", "Нужно очень многоработать",
                epic2.getId());
        taskManager.addSubTask(subTask10);

        //Печатаем список сабтасков первого эпика
        System.out.println("смотри список сабтасков у эпика 1" + taskManager.getEpicSubtasks(epic1.getId()));

        // Меняем состояние первого сабтаска первого эпика на IN_PROGRESS
        SubTask subTask4 = new SubTask("ИЗМЕНЕНИЕ", "Неплохо...", Status.IN_PROGRESS, subTask1.getId(),
                epic1.getId());
        taskManager.updateSubTask(subTask4);

        System.out.println("смотрим НВОЫЙ статус у эпика 1 (должен быть IN_PROGRESS) = " + epic1.getStatus());
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ" + taskManager.getAllEpics());
        //Запрашиваем по ID для того, что бы наполнить историю просмотров
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());


        //Изменяем все сабтаски первого эпика на DONE
        SubTask subTask5 = new SubTask("ИЗМЕНЕНИЕ", "Неплохо...", Status.DONE, subTask1.getId(),
                epic1.getId());
        SubTask subTask6 = new SubTask("ИЗМЕНЕНИЕ2", "...", Status.DONE, subTask2.getId(),
                epic1.getId());

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

        System.out.println("ПЕЧАТАЕМ ИСТОРИЮ ПРОСМОТРА " + taskManager.getHistory());

    }
}
