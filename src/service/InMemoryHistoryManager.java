package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;


class InMemoryHistoryManager implements HistoryManager {
    static List<Task> historyList = new ArrayList<>(10);

    @Override
    public List<Task> getDefaultHistory() {
        return historyList;
    }


    //Метод по добавлению в список тасок, с ограничением в 10шт

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyList.size() < 10) {
                historyList.add(task);
            } else {
                historyList.remove(0);
                historyList.add(9, task);
            }
        }
    }

}
