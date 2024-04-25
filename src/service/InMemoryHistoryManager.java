package service;

import java.util.*;

import model.Task;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> historyMapList = new HashMap<>();
    //Первый элемент списка
    private Node head;
    //Последний элемент списка
    private Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    public List<Task> getTasks() {
        List<Task> tempTaskList = new ArrayList<>();
        Node nodeTemp = head;
        while (nodeTemp != null) {
            tempTaskList.add(nodeTemp.data);
            nodeTemp = nodeTemp.next;
        }
        return tempTaskList;
    }

    public void linkLast(Task task) {
        final Node newNode = new Node(task, tail);
        tail = newNode;
        if (head == null) {
            head = newNode;
        } else {
            tail.prev.next = newNode;
        }
        historyMapList.put(task.getId(), newNode);
    }

    @Override
    public void remove(int taskId) {
        Node node = historyMapList.get(taskId);
        removeNode(node);
    }

    private void removeNode(Node node) {
        if (node != null) {
            if (node.equals(head)) {
                head = node.next;
            } else if (node.equals(tail)) {
                node.prev.next = null;
                tail = node.prev;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            historyMapList.remove(node.data.getId());
        }
    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Task task, Node prevNode) {
            this.data = task;
            this.prev = prevNode;
            this.next = null;
        }
    }
}