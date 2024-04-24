package service;

import java.util.Map;
import model.Epic;
import constants.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int totalTasks;
    Map<Integer, Task> tasks;
    Map<Integer, Epic> epics;
    Map<Integer, SubTask> subTasks;
    HistoryManager historyManager;

    InMemoryTaskManager() {
        totalTasks = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        int tempId = changeIdCounter();
        task.setId(tempId);
        tasks.put(tempId, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int tempId = changeIdCounter();
        epic.setId(tempId);
        epics.put(tempId, epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        int tempId = changeIdCounter();
        subTask.setId(tempId);
        subTasks.put(tempId, subTask);
        updateEpicStatus(subTask.getEpicId());
    }


    private void updateEpicStatus(int idEpic) {
        int numberSubTaskDone = 0;
        int forEpicSubTaskNumber = 0;
        int numberSubTaskNew = 0;
        if (subTasks.isEmpty()) {
            epics.get(idEpic).setStatus(Status.NEW);
            return;
        }
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
                epics.get(idEpic).setStatus(Status.IN_PROGRESS);
                return;
            }
        }

        //устанавливаем статус эпика по результатам подсчета
        if (forEpicSubTaskNumber == numberSubTaskDone && forEpicSubTaskNumber > 0) {
            epics.get(idEpic).setStatus(Status.DONE);
        } else if (forEpicSubTaskNumber == numberSubTaskNew) {
            epics.get(idEpic).setStatus(Status.NEW);
        } else {
            epics.get(idEpic).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    //Обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //Получение всех сабтасков по ID эпика
    @Override
    public List<SubTask> getEpicSubtasks(int id) {
        List<SubTask> subTaskList = new ArrayList<>();

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

    @Override
    public Task getTaskById(int idTask) {
        Task task = tasks.get(idTask);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int idEpic) {
        Epic epic = epics.get(idEpic);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int idSubTask) {
        SubTask subTask = subTasks.get(idSubTask);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return new ArrayList<>(epics.values());
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        if (epics.isEmpty()) {
            System.out.println("Список сабтасок пуст");
            return new ArrayList<>(subTasks.values());
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getAllTask() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return new ArrayList<>(tasks.values());
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void dellSubTaskById(int idSubTask) {
        SubTask tempSubTask = subTasks.get(idSubTask);
        historyManager.remove(idSubTask);
        subTasks.remove(idSubTask);
        updateEpicStatus(tempSubTask.getEpicId());
        System.out.println("Удалили Subtask id=" + idSubTask);
    }

    @Override
    public void dellAllSubTasks() {
        subTasks.clear();
        System.out.println("Список сабтасков очищен");
        for (Epic epic : epics.values()) {
            epics.get(epic.getId()).setStatus(Status.NEW);
        }
    }

    @Override
    public void dellAllEpics() {
        for (Integer idEpic: epics.keySet()) {
            historyManager.remove(idEpic);
        }
        for (Integer idSubTask: subTasks.keySet()) {
            historyManager.remove(idSubTask);
        }
        epics.clear();
        subTasks.clear();
        System.out.println("Список эпиков и сабтасков очищен");
    }

    @Override
    public void dellEpicById(int idEpic) {
        if (epics.containsKey(idEpic)) {
            HashMap<Integer, SubTask> newSubTasks = new HashMap<>();
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() != idEpic) {
                    newSubTasks.put(subTask.getId(), subTask);
                }
            }
            if (newSubTasks != null) {
                subTasks = newSubTasks;
            }
            historyManager.remove(idEpic);
            epics.remove(idEpic);

        } else {
            System.out.println("Такого эпика нет!");
        }
    }

    @Override
    public void delTaskById(int idTask) {
        historyManager.remove(idTask);
        tasks.remove(idTask);
        System.out.println("Удалили TASK id=" + idTask);
    }

    @Override
    public void delAllTask() {
        for (Integer taskId: tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
        System.out.println("Список задач очищен");
    }
}