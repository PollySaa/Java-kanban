package service;

import components.Epic;
import components.Status;
import components.Subtask;
import components.Task;
import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public void save() {
        String header = "id,type,name,status,description,epic,startTime,duration,endTime" + "\n";
        try (FileWriter fileRecord = new FileWriter(path.toString())) {
            fileRecord.write(header);
            for (Integer key : tasks.keySet()) {
                String str = tasks.get(key).toString() + "\n";
                fileRecord.write(str);
            }
            for (Integer key : epics.keySet()) {
                String str = epics.get(key).toString() + "\n";
                fileRecord.write(str);
            }
            for (Integer key : subtasks.keySet()) {
                String str = subtasks.get(key).toString() + "\n";
                fileRecord.write(str);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        Path path = Paths.get(file.toString());
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), path);
        fileManager.load();
        return fileManager;
    }

    public void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                if (line.isEmpty()) {
                    break;
                }

                Task task = fromString(line);
                switch (task.getType()) {
                    case TASK:
                        tasks.put(task.getId(), task);
                        prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        if (epic.getStartTime() != null) {
                            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
                        }
                        epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        subtasks.put(task.getId(), subtask);
                        prioritizedTasks.add(task);
                        Epic e = epics.get(subtasks.get(subtask.getId()).getIdEpic());
                        e.addSubtasks(subtask);
                        break;
                }
            }

            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                List<Integer> list = historyFromString(line);
                for (Integer id : list) {
                    if (tasks.containsKey(id)) {
                        historyManager.add(tasks.get(id));
                    }
                    if (epics.containsKey(id)) {
                        historyManager.add(epics.get(id));
                    }
                    if (subtasks.containsKey(id)) {
                        historyManager.add(subtasks.get(id));
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка чтения из файла", e);
        }
    }

    private static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Status status = Status.valueOf(parts[3]);
        Type type = Type.valueOf(parts[1]);
        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, type, parts[2], status, parts[4], LocalDateTime.parse(parts[5]),
                        Duration.parse(parts[6]));
                break;
            case EPIC:
                task = new Epic(id, type, parts[2], status, parts[4]);
                if (!parts[5].equals("null")) {
                    task.setStartTime(LocalDateTime.parse(parts[5]));
                } else {
                    task.setStartTime(null);
                }
                if (!parts[7].equals("null")) {
                    task.setDuration(Duration.parse(parts[6]));
                } else {
                    task.setDuration(null);
                }
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7]);
                task = new Subtask(id, type, parts[2], parts[4], status, LocalDateTime.parse(parts[5]),
                        Duration.parse(parts[6]), epicId);
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип объекта " + type);

        }
        return task;
    }

    @Override
    public Integer getId() {
        Integer id = super.getId();
        save();
        return id;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> task = super.getAllTask();
        save();
        return task;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtask = super.getAllSubtask();
        save();
        return subtask;
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> epic = super.getAllEpic();
        save();
        return epic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getAllTasksById(Integer id) {
        Task task = super.getAllTasksById(id);
        save();
        return task;
    }

    @Override
    public Subtask getAllSubtasksById(Integer id) {
        Subtask subtask = super.getAllSubtasksById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getAllEpicsById(Integer id) {
        Epic epic = super.getAllEpicsById(id);
        save();
        return epic;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksByIdEpic(Epic epic) {
        ArrayList<Subtask> subtasks = super.getSubtasksByIdEpic(epic);
        save();
        return subtasks;
    }

    @Override
    public ArrayList<Task> getHistoryTasks() {
        ArrayList<Task> history = super.getHistoryTasks();
        save();
        return history;
    }
}
