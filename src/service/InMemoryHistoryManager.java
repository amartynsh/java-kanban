package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>(10);

    @Override
    public List<Task> getHistory() {
        return List.copyOf(historyList);
    }

    //Метод по добавлению в список тасок, с ограничением в 10шт
    @Override
    public void add(Task task) {

        if (task != null) {
            int HISTORY_SIZE = 10;
            if (historyList.size() >= HISTORY_SIZE) {
                historyList.remove(0);
            }
            historyList.add(task);
        }
    }

}
