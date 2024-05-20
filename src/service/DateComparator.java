package service;

import model.Task;

import java.util.Comparator;


public class DateComparator implements Comparator<Task> {


    @Override
    public int compare(Task o1, Task o2) {
        int result = 1;
        if (o1.getStartTime() != null && o2.getStartTime() != null) {
            result = o1.getStartTime().compareTo(o2.getStartTime());
        }
        return result;
    }
}