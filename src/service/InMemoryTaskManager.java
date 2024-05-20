package service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import exceptions.*;
import model.Epic;
import constants.Status;
import manager.Managers;
import model.SubTask;
import model.Task;

public class InMemoryTaskManager implements TaskManager {
    private int totalTasks;
    Map<Integer, Task> tasks;
    Map<Integer, Epic> epics;
    Map<Integer, SubTask> subTasks;
    HistoryManager historyManager;
    Set<Task> sortedTasks;

    public InMemoryTaskManager() {
        totalTasks = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        sortedTasks = new TreeSet<>(new DateComparator());
    }

    @Override
    public void addTask(Task task) {

        if (!checkCrossTaskTime(task)) {
            int tempId = changeIdCounter();
            task.setId(tempId);
            tasks.put(tempId, task);
            sortTasksListAdd(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        int tempId = changeIdCounter();
        epic.setId(tempId);
        epics.put(tempId, epic);
        sortTasksListAdd(epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {

        int tempId = changeIdCounter();
        subTask.setId(tempId);
        subTasks.put(tempId, subTask);
        updateEpicStatus(subTask.getEpicId());
        calcEpicTimesAndDuration(subTask.getEpicId());
        sortTasksListAdd(subTask);

    }

    @Override
    public SubTask getSubTaskById(int idSubTask) {
        SubTask subTask = subTasks.get(idSubTask);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Task getTaskById(int idTask) {
        Task task = tasks.get(idTask);
        historyManager.add(task);
        return task;
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
    public Epic getEpicById(int idEpic) {
        Epic epic = epics.get(idEpic);
        historyManager.add(epic);
        return epic;
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
        calcEpicTimesAndDuration(subTask.getEpicId());
        sortTasksListAdd(subTask);
    }


    //Обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        sortTasksListAdd(epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        sortTasksListAdd(task);
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

    @Override
    public List<Task> getAllTask() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return new ArrayList<>(tasks.values());
        }
        return new ArrayList<>(tasks.values());
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
    public void dellEpicById(int idEpic) {
        if (epics.containsKey(idEpic)) {
            HashMap<Integer, SubTask> newSubTasks = new HashMap<>();
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() != idEpic) {
                    newSubTasks.put(subTask.getId(), subTask);
                } else {
                    dellTaskFromSortedList(subTask);
                }

            }
            if (newSubTasks != null) {
                subTasks = newSubTasks;
            }
            historyManager.remove(idEpic);
            dellTaskFromSortedList(epics.get(idEpic));
            epics.remove(idEpic);

        } else {
            System.out.println("Такого эпика нет!");
        }
    }

    @Override
    public void dellAllEpics() {
        for (Integer idEpic : epics.keySet()) {
            historyManager.remove(idEpic);
            dellTaskFromSortedList(epics.get(idEpic));
        }
        for (Integer idSubTask : subTasks.keySet()) {
            historyManager.remove(idSubTask);
            dellTaskFromSortedList(subTasks.get(idSubTask));
        }
        epics.clear();
        subTasks.clear();
    }


    protected void changeTotalTask(int newTotal) {
        totalTasks = newTotal;
    }

    @Override
    public void dellAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epics.get(epic.getId()).setStatus(Status.NEW);
        }
    }

    @Override
    public void delAllTask() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
        System.out.println("Список задач очищен");
    }

    @Override
    public void dellSubTaskById(int idSubTask) {
        SubTask tempSubTask = subTasks.get(idSubTask);
        historyManager.remove(idSubTask);
        dellTaskFromSortedList(subTasks.get(idSubTask));
        subTasks.remove(idSubTask);
        updateEpicStatus(tempSubTask.getEpicId());
    }

    @Override
    public void delTaskById(int idTask) {
        dellTaskFromSortedList(tasks.get(idTask));
        historyManager.remove(idTask);
        tasks.remove(idTask);
    }

    //изменение ID. ID единый для всех типов задач
    private int changeIdCounter() {
        return totalTasks++;
    }

    public void calcEpicDuration(int idEpic) {
        Duration epicDuration = null;
        if (epics.containsKey(idEpic)) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() == idEpic) {
                    if (epicDuration != null) {
                        epicDuration = epicDuration.plus(subTask.getDuration());
                    } else {
                        epicDuration = subTask.getDuration();
                    }
                }
            }
            epics.get(idEpic).setDuration(epicDuration);
        }
    }

    private void calcEpicTimesAndDuration(int idEpic) {
        calcEpicStartTime(idEpic);
        calcEpicEndTime(idEpic);
        calcEpicDuration(idEpic);
        sortTasksListAdd(epics.get(idEpic));
    }

    private void calcEpicStartTime(int idEpic) {
        LocalDateTime epicStartTime = null;
        if (epics.containsKey(idEpic)) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() == idEpic && subTask.getStartTime() != null) {
                    if (epicStartTime == null) {
                        epicStartTime = subTask.getStartTime();
                    } else if (epicStartTime.isAfter(subTask.getStartTime())) {
                        epicStartTime = subTask.getStartTime();
                    }
                }
            }
            epics.get(idEpic).setStartTime(epicStartTime);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void calcEpicEndTime(int idEpic) {
        LocalDateTime epicEndTime = null;
        Duration epicDuration = null;

        if (epics.containsKey(idEpic)) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() == idEpic && subTask.getDuration() != null) {
                    if (epicDuration == null) {
                        epicDuration = subTask.getDuration();
                    } else {
                        epicDuration = epicDuration.plus(subTask.getDuration());
                    }
                }
            }
            if (epicDuration != null) {
                epicEndTime = epics.get(idEpic).getStartTime().plus(epicDuration);
                epics.get(idEpic).setEndTime(epicEndTime);
            }
        }
    }

    public boolean checkCrossTaskTime(Task task) {
        //Проверяем, что время начала таска между датой начала и окончания таска, а так же проверяем
        // что дата окончания не ходится между датой начала и окончания таска
        boolean checkResult = false;
        try {
            checkResult = sortedTasks.stream()

                    .anyMatch(task1 -> (task.getStartTime().isAfter(task1.getStartTime()) &&
                            task.getStartTime().isBefore(task1.getEndTime())) ||
                            (task.getEndTime().isAfter(task1.getStartTime()) &&
                                    task.getEndTime().isBefore(task1.getEndTime())) ||
                            //Сначала проверяем на больше/меньше, потом на время равно
                            task.getStartTime().isEqual(task1.getStartTime()) ||
                            task.getEndTime().isEqual(task1.getEndTime()));
            if (checkResult) {
                throw new ManagerCheckException("Время у добавляемой задачи пересекатеся:" + " " + task.getClass() +
                        " " + task.getName());
            }
        } catch (ManagerCheckException e) {
            System.out.println(e.getMessage());
        }
        return checkResult;
    }

    public void sortTasksListAdd(Task task) {

        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    public void dellTaskFromSortedList(Task task) {
        if (task.getStartTime() != null) {
            sortedTasks.remove(task);
        }
    }
}