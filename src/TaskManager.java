import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public static int totalTasks = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();

    HashMap<Integer, Epic> epics = new HashMap<>();

    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public void addTask(Task task) {
        int tempId = changeIdCounter();
        tasks.put(tempId, task);
        task.setId(tempId);
    }

    public void addEpic(Epic epic) {
        int tempId = changeIdCounter();
        epics.put(tempId, epic);
        epic.setId(tempId);
    }

    public void addSubTask(int idEpic, SubTask subTask) {
        int tempId = changeIdCounter();
        subTask.setId(tempId);
        subTask.setEpicId(idEpic);
        subTasks.put(tempId, subTask);
    }

    public void updateSubTask(int idSubTask, SubTask subTask) {
        int epicId = subTask.getEpicId();
        subTask.setId(idSubTask);
        subTasks.put(idSubTask, subTask);

        //Если статус сабтаска изменился, то ставим статус эпика IN_PROGRESS
        if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
            epics.get(subTask.getEpicId()).setStatus(Status.IN_PROGRESS);
            //Если потавили статус сабтаска DONE, то проверяем остальные сбатаски эпика и если надо меняем его статус
        } else if (subTask.getStatus().equals(Status.DONE)) {
            checkAreAllSubTaskIsDone(epicId);
        }
    }
    //Обновление эпика
    public void updateEpic(int idEpic, Epic epic) {
        epic.setId(idEpic);
        epics.put(idEpic, epic);
    }
    //Метод для проверки и изменения статуса эпика на DONE
    public void checkAreAllSubTaskIsDone(int idEpic) {
        int numberSubTaskDone = 0;
        int forEpicSubTaskNumber = 0;
        for (SubTask subTaskForCheck : subTasks.values()) {
            if (subTaskForCheck.getEpicId() == idEpic) {
                forEpicSubTaskNumber = forEpicSubTaskNumber + 1;
                if (subTaskForCheck.getStatus().equals(Status.DONE) && subTaskForCheck.getEpicId() == idEpic) {
                    numberSubTaskDone = numberSubTaskDone + 1;
                    if (forEpicSubTaskNumber == numberSubTaskDone) {
                        epics.get(idEpic).setStatus(Status.DONE);
                    }
                }
            }
        }
    }
//Получение всех сабтасков по ID эпика
    public ArrayList<SubTask> getEpicSubtasks(int id) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTaskList.add(subTask);
            }
        }
        return subTaskList;
    }

    //изменение ID. ID единый для всех типов задач
    public static int changeIdCounter() {
        int id = totalTasks;
        totalTasks = totalTasks + 1;
        return id;
    }

    public Task getTaskById(int idTask) {
        Task task = tasks.get(idTask);
        return task;
    }

    public void delTaskById(int idTask) {
        tasks.remove(idTask);
        System.out.println("Удалили TASK id=" + idTask);
    }

    public void delAllTask() {
        tasks.clear();
        System.out.println("Список задач очищен");
    }

    public void updateTask(int idTask, Task task) {
        task.setId(idTask);
        tasks.put(idTask, task);
    }

    public ArrayList<Task> getAllTask() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (int taskId : tasks.keySet()) {
            tasksList.add(tasks.get(taskId));
        }
        return tasksList;
    }
    public Epic getEpicById(int idEpic) {
        Epic epic = epics.get(idEpic);
        return epic;
    }

    public void dellEpicById(int idEpic) {
        epics.remove(idEpic);
        System.out.println("Удалили EPIC id=" + idEpic);
    }
    public void dellAllEpics() {
        epics.clear();
        System.out.println("Список эпиков очищен");
    }

    public SubTask getSubTaskById (int idSubTask) {
        SubTask subTask = subTasks.get(idSubTask);
        return subTask;
    }

    public void dellSubTaskById(int idSubTask) {
        epics.remove(idSubTask);
        System.out.println("Удалили Subtask id=" + idSubTask);
    }
    public void dellAllSubTasks() {
        subTasks.clear();
        System.out.println("Список сабтасков очищен");
    }
}
