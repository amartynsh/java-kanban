import java.util.HashMap;

public class TaskManager {
    public static int totalTasks = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(newId(), task);
    }

    public int newId() {
        int id = totalTasks;
        totalTasks = totalTasks + 1;
        return id;
    }

    public void getTaskById(int id) {

        System.out.println(tasks.get(id));

    }

    public void delTaskById(int id) {

        tasks.remove(id);
        System.out.println("Удалили id=" + id);

    }

    public void delAllTask() {

        tasks.clear();
        System.out.println("Список задач очищен");

    }
}