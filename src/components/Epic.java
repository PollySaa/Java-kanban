package components;

import service.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public Epic(Integer id, Type type, String taskName, Status status, String taskDescription) {
        super(id, type, taskName, status, taskDescription);
    }

    public void addSubtasks(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtasks(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void removeSubtasks(Integer id) {
        subtasks.remove(id);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
        }

        int counterDone = 0;
        int counterNew = 0;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == Status.NEW) {
                counterNew++;
            } else if (subtask.getStatus() == Status.DONE) {
                counterDone++;
            }
        }

        if (counterNew == subtasks.size()) {
            setStatus(Status.NEW);
        } else if (counterDone == subtasks.size()) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription();
    }
}

