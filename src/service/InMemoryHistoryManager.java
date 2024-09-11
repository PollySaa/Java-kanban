package service;

import components.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> historyTask = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyTask.containsKey(task.getId())) {
            removeNode(historyTask.get(task.getId()));
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = historyTask.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        historyTask.put(task.getId(), newNode);
    }

    @Override
    public void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        historyTask.remove(node.task.getId());
    }
}
