import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Доработать работу 1", "Работа плохо проработана, поработай");
        Task task2 = new Task("Переработать работу 2", "Неплохо, неплохо... но еще поработай");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
// распечатаем список тасков
        for (int taskId: taskManager.tasks.keySet()) {
            System.out.println("Task id=" + taskId + " "+ taskManager.tasks.get(taskId));
        }
    }
}
