package service;

import components.Epic;
import components.Subtask;
import components.Task;

import java.util.ArrayList;

public interface TaskManager {

    Integer getId();

    ArrayList<Task> getAllTask();

    ArrayList<Subtask> getAllSubtask();

    ArrayList<Epic> getAllEpic();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getAllTasksById(Integer id);

    Subtask getAllSubtasksById(Integer id);

    Epic getAllEpicsById(Integer id);

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(Integer id);

    void removeSubtaskById(Integer id);

    void removeEpicById(Integer id);

    ArrayList<Subtask> getSubtasksByIdEpic(Epic epic);

    ArrayList<Task> getHistoryTasks();

    boolean equals(Object o);

    int hashCode();

    ArrayList<Task> getPrioritizedTasks();

    void epicDateTime(Epic epic);

}

