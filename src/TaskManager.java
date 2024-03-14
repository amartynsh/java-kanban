import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public int totalTasks = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();

    HashMap<Integer, Epic> epics = new HashMap<>();

    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public void addTask(Task task) {
        int tempId = changeIdCounter();
        task.setId(tempId);
        tasks.put(tempId, task);
    }

    public void addEpic(Epic epic) {
        int tempId = changeIdCounter();
        epic.setId(tempId);
        epics.put(tempId, epic);

    }

    public void addSubTask(SubTask subTask) {
        int tempId = changeIdCounter();
        subTask.setId(tempId);
        subTasks.put(tempId, subTask);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.id, subTask);
        updateEpicStatus(subTask);
    }

    private void updateEpicStatus(SubTask subTask) {
        int idEpic = subTask.getEpicId();
        int numberSubTaskDone = 0;
        int forEpicSubTaskNumber = 0;
        int numberSubTaskNew = 0;
        int numberSubTaskInProgress = 0;

        //считаем количество сабтасков у эпика с разным статусом
        for (SubTask subTaskForCheck : subTasks.values()) {
            if (subTaskForCheck.getEpicId() == idEpic) {
                forEpicSubTaskNumber = forEpicSubTaskNumber + 1;
            }
            if (subTaskForCheck.getStatus().equals(Status.DONE) && subTaskForCheck.getEpicId() == idEpic) {
                numberSubTaskDone = numberSubTaskDone + 1;
            }
            if (subTaskForCheck.getStatus().equals(Status.NEW) && subTaskForCheck.getEpicId() == idEpic) {
                numberSubTaskNew = numberSubTaskNew + 1;
            }
            if (subTaskForCheck.getStatus().equals(Status.IN_PROGRESS) && subTaskForCheck.getEpicId() ==
                    idEpic) {
                numberSubTaskInProgress = numberSubTaskInProgress + 1;
            }
        }

        //устанавливаем статус эпика по результатам подсчета
        if (forEpicSubTaskNumber == numberSubTaskDone) {
            epics.get(idEpic).setStatus(Status.DONE);
        } else if (numberSubTaskNew == forEpicSubTaskNumber) {
            epics.get(idEpic).setStatus(Status.NEW);
        } else if (numberSubTaskInProgress > 0) {
            epics.get(subTask.getEpicId()).setStatus(Status.IN_PROGRESS);
        }
    }

    //Обновление эпика
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
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
    private int changeIdCounter() {
        return totalTasks++;
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

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
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
        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == idEpic) {
                subTasks.remove(subtask.getId());
            }
        }
        epics.remove(idEpic);
        System.out.println("Удалили EPIC id=" + idEpic + " а так же все его сабтаски");

    }

    public void dellAllEpics() {
        epics.clear();
        subTasks.clear();
        System.out.println("Список эпиков и сабтасков очищен");
    }

    public SubTask getSubTaskById(int idSubTask) {
        SubTask subTask = subTasks.get(idSubTask);
        return subTask;
    }

    public void dellSubTaskById(int idSubTask) {
        SubTask tempSubTask = subTasks.get(idSubTask);
        subTasks.remove(idSubTask);
        updateEpicStatus(tempSubTask);
        System.out.println("Удалили Subtask id=" + idSubTask);
    }

    public void dellAllSubTasks() {
        subTasks.clear();
        System.out.println("Список сабтасков очищен");
    }
}
