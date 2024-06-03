package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    List<SubTask> getEpicSubtasks(int id);

    Task getTaskById(int idTask);

    Epic getEpicById(int idEpic);

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    SubTask getSubTaskById(int idSubTask);

    List<Task> getAllTask();

    List<Task> getHistory();

    void delTaskById(int idTask);

    void delAllTask();

    void dellEpicById(int idEpic);

    void dellAllEpics();

    void dellSubTaskById(int idSubTask);

    void dellAllSubTasks();

    List<Task> getPrioritizedTask();

}
