package service;

import components.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    ArrayList<Task> getHistory();

    void linkLast(Task task);

    void removeNode(Node node);
}
