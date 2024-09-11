package service;

import components.Epic;
import components.Subtask;
import components.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;
    private final String exception = "Задача пересекается по времени с другой задачей.";

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = Managers.getDefaultHistory();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(taskComparator);
    }

    @Override
    public Integer getId() {
        return ++id;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values()
                .forEach(task -> {
                    prioritizedTasks.remove(task);
                    historyManager.remove(task.getId());
                });
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values()
                .forEach(subtask -> {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtask.getId());
                });
        subtasks.clear();

        epics.values()
                .forEach(epic -> {
                    epic.removeAllSubtasks();
                    epic.updateStatus();
                    epicDateTime(epic);
                });
    }

    @Override
    public void removeAllEpics() {
        subtasks.values()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                });

        epics.values()
                .forEach(epic -> {
                    historyManager.remove(epic.getId());
                });
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getAllTasksById(Integer id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getAllSubtasksById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }

        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getAllEpicsById(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }

        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }

        if (isTimeOverLapping(task)) {
            throw new IllegalArgumentException(exception);
        }
        task.setId(getId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        if (isTimeOverLapping(subtask)) {
            throw new IllegalArgumentException(exception);
        }

        if (epics.containsKey(subtask.getIdEpic())) {
            subtask.setId(getId());
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            epic.addSubtasks(subtask);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        epic.setId(getId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdEpic())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            epic.updateSubtasks(subtask);
            epic.updateStatus();
            epicDateTime(epic);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            epic.updateStatus();
        }
    }

    @Override
    public void removeTaskById(Integer id) {
        if (tasks.containsKey(id)) { // Проверяем, есть ли задача с таким ID
            historyManager.remove(id);
            tasks.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
    }

    @Override
    public void removeSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            historyManager.remove(id);
            Epic epic = getAllEpicsById(subtasks.get(id).getIdEpic());
            epic.removeSubtasks(id);
            epic.updateStatus();
            subtasks.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
            epicDateTime(epic);
        }
    }

    @Override
    public void removeEpicById(Integer id) {
        if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByIdEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return epic.getSubtasks();
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistoryTasks() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void epicDateTime(Epic epic) {
        Set<Integer> subtaskList = epic.getSubtasksKeys();
        if (subtaskList.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        for (Integer subtaskId : subtaskList) {
            LocalDateTime subtaskStartTime = subtasks.get(subtaskId).getStartTime();
            LocalDateTime subtaskEndTime = subtasks.get(subtaskId).getEndTime();
            Duration subtaskDuration = subtasks.get(subtaskId).getDuration();
            if (epic.getStartTime() == null) {
                epic.setStartTime(subtaskStartTime);
                epic.setEndTime(subtaskEndTime);
                if (epic.getDuration() == null) {
                    epic.setDuration(subtaskDuration);
                } else {
                    epic.setDuration(epic.getDuration().plus(subtaskDuration));
                }
            } else {
                if (epic.getStartTime().isAfter(subtaskStartTime)) {
                    epic.setStartTime(subtaskStartTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subtaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subtaskDuration));
                    }
                }
                if (subtaskEndTime.isAfter(epic.getEndTime())) {
                    epic.setEndTime(subtaskEndTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subtaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subtaskDuration));
                    }
                }
            }
        }
    }

    private boolean isTimeOverLapping(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime finish = task.getEndTime();
        if (start == null) {
            return false;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime begin = prioritizedTask.getStartTime();
            LocalDateTime end = prioritizedTask.getEndTime();

            if (start.isEqual(begin) || start.isEqual(end) || finish.isEqual(end) || finish.isEqual(begin)) {
                return true;
            }
            if ((start.isAfter(begin) && start.isBefore(end)) || (finish.isAfter(begin) && finish.isBefore(end))) {
                return true;
            }
            if (start.isBefore(begin) && finish.isAfter(end)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    Comparator<Task> taskComparator = (o1, o2) -> {
        if (o1 == null || o2 == null) {
            return 0;
        }

        if (Objects.equals(o1.getId(), o2.getId())) {
            return 0;
        }

        if (o1.getStartTime() == null) {
            return 1;
        }

        if (o2.getStartTime() == null) {
            return -1;
        }

        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return o1.getId() - o2.getId();
        }
    };
}
