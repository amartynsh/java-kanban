package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getDefaultHistory();

    void add(Task task);
}
