package service;

import components.Epic;
import components.Status;
import components.Subtask;
import components.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager;
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;
    LocalDateTime startTime1;
    LocalDateTime startTime2;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();

        startTime1 = LocalDateTime.of(2024, 6, 28, 1, 0);
        startTime2 = LocalDateTime.of(2024, 6, 28, 1, 10);

        task1 = new Task(Type.TASK, "Task1", Status.NEW, "Empty", startTime1,
                Duration.ofMinutes(9));
        task2 = new Task(Type.TASK, "Task2", Status.NEW, "Empty", startTime2,
                Duration.ofMinutes(5));
        manager.addTask(task1);
        manager.addTask(task2);

        epic1 = new Epic(Type.EPIC,"Epic1", Status.NEW, "Empty");
        manager.addEpic(epic1);

        subtask1 = new Subtask(Type.SUBTASK, "Subtask1", Status.NEW, "Empty",
                startTime1.plusMinutes(60), Duration.ofMinutes(9), 3);
        subtask2 = new Subtask(Type.SUBTASK, "Subtask2", Status.NEW, "Empty",
                startTime2.plusMinutes(60), Duration.ofMinutes(5), 3);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
    }

    @Test
    public void getAllTask() {
        assertEquals(2, manager.getAllTask().size());
        assertEquals(task1, manager.getAllTask().get(0));
        assertEquals(task2, manager.getAllTask().get(1));
    }

    @Test
    public void getAllSubtask() {
        assertEquals(2, manager.getAllSubtask().size());
        assertEquals(subtask1, manager.getAllSubtask().get(0));
        assertEquals(subtask2, manager.getAllSubtask().get(1));
    }

    @Test
    public void getAllEpic() {
        assertEquals(1, manager.getAllEpic().size());
        assertEquals(epic1, manager.getAllEpic().getFirst());;
    }

    @Test
    public void removeAllTasks() {
        manager.removeAllTasks();

        assertEquals(0, manager.getAllTask().size());
    }

    @Test
    public void removeAllSubtasks() {
        manager.removeAllSubtasks();

        assertEquals(0, manager.getAllSubtask().size());
    }

    @Test
    public void removeAllEpics() {
        manager.removeAllEpics();

        assertEquals(0, manager.getAllEpic().size());
    }

    @Test
    public void getAllTasksById() {
        assertEquals(task1, manager.getAllTasksById(1));
        assertEquals(task2, manager.getAllTasksById(2));
    }

    @Test
    public void getAllTasksByIdWithWrongId() {
        assertNull(manager.getAllTasksById(6));
    }

    @Test
    public void getAllSubtasksById() {
        assertEquals(subtask1, manager.getAllSubtasksById(4));
        assertEquals(subtask2, manager.getAllSubtasksById(5));
    }

    @Test
    public void getAllSubtasksByIdWithWrongId() {
        assertNull(manager.getAllSubtasksById(8));
    }

    @Test
    public void getAllEpicsById() {
        assertEquals(epic1, manager.getAllEpicsById(3));
    }

    @Test
    public void getAllEpicsByIdWithWrongId() {
        assertNull(manager.getAllEpicsById(8));
    }

    @Test
    public void addTask() {
        assertEquals(2, manager.getAllTask().size());
        assertEquals(task1, manager.getAllTask().getFirst());
        assertEquals(task2, manager.getAllTask().getLast());
    }

    @Test
    public void addTaskNull() {
        assertDoesNotThrow(() -> manager.addTask(null));
    }

    @Test
    public void addSubtask() {
        assertEquals(2, manager.getAllSubtask().size());
        assertEquals(subtask1, manager.getAllSubtask().get(0));
        assertEquals(subtask2, manager.getAllSubtask().get(1));
        assertEquals(epic1.getId(), subtask1.getIdEpic());
        assertEquals(epic1.getId(), subtask2.getIdEpic());
    }

    @Test
    public void addSubtaskNull() {
        assertDoesNotThrow(() -> manager.addSubtask(null));
    }

    @Test
    public void addEpic() {
        assertEquals(1, manager.getAllEpic().size());
        assertEquals(epic1, manager.getAllEpic().getFirst());
    }

    @Test
    public void addEpicNull() {
        assertDoesNotThrow(() -> manager.addEpic(null));
    }

    @Test
    public void updateTask() {
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        task2.setStatus(Status.DONE);
        manager.updateTask(task2);

        assertEquals(2, manager.getAllTask().size());
        assertEquals(task1, manager.getAllTask().getFirst());
        assertEquals(task2, manager.getAllTask().getLast());
    }

    @Test
    public void updateTaskNull() {
        assertDoesNotThrow(() -> manager.updateTask(null));
    }

    @Test
    public void updateSubtask() {
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        assertEquals(2, manager.getAllSubtask().size());
        assertEquals(subtask1, manager.getAllSubtask().getFirst());
        assertEquals(subtask2, manager.getAllSubtask().getLast());
    }

    @Test
    public void updateSubtaskNull() {
        assertDoesNotThrow(() -> manager.updateSubtask(null));
    }

    @Test
    public void updateEpic() {
        Epic epic1 = new Epic("Эпик1", "Пусто");
        manager.addEpic(epic1);

        assertEquals(Status.NEW, epic1.getStatus());

        Subtask subtask1 = new Subtask("Подзадача1", "Пусто", epic1.getId());
        manager.addSubtask(subtask1);

        assertEquals(Status.NEW, epic1.getStatus());

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateEpic(epic1);

        assertEquals(Status.DONE, epic1.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateEpic(epic1);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void updateEpicNull() {
        assertDoesNotThrow(() -> manager.updateEpic(null));
    }

    @Test
    public void removeTaskById() {
        assertEquals(2, manager.getAllTask().size());

        manager.removeTaskById(task1.getId());

        assertEquals(1, manager.getAllTask().size());
        assertEquals(task2, manager.getAllTask().getFirst());
    }

    @Test
    public void removeSubtaskById() {
        assertEquals(2, manager.getAllSubtask().size());

        manager.removeSubtaskById(subtask1.getId());

        assertEquals(1, manager.getAllSubtask().size());
        assertEquals(subtask2, manager.getAllSubtask().getFirst());
    }

    @Test
    public void removeEpicById() {
        assertEquals(subtask1, epic1.getSubtasks().getFirst());
        assertEquals(subtask2, epic1.getSubtasks().getLast());
        assertEquals(1, manager.getAllEpic().size());

        manager.removeEpicById(epic1.getId());

        assertEquals(0, manager.getAllEpic().size());
    }

    @Test
    public void getSubtasksByIdEpic() {
        ArrayList<Subtask> subtasks = manager.getSubtasksByIdEpic(epic1);
        assertEquals(epic1.getSubtasks(), subtasks);
    }

    @Test
    public void getHistoryTasks() {
        manager.getAllTasksById(1);
        manager.getAllEpicsById(3);
        manager.getAllSubtasksById(4);

        ArrayList<Task> tasks1 = new ArrayList<>();
        tasks1.add(task1);
        tasks1.add(epic1);
        tasks1.add(subtask1);

        ArrayList<Task> tasks2 = manager.getHistoryTasks();

        assertEquals(tasks2, tasks1);
    }

    @Test
    public void epicStartTimeDurationAndEndTimeShouldChangeWithSubTasks() {
        manager.epicDateTime(epic1);
        assertEquals(startTime1.plusMinutes(60), epic1.getStartTime());
        assertEquals(startTime2.plusMinutes(60).plus(Duration.ofMinutes(5)), epic1.getEndTime());
        assertEquals(Duration.ofMinutes(14), epic1.getDuration());
    }

    @Test
    public void updateSubTaskWithCheck() {
        Subtask subtask = new Subtask(4, Type.SUBTASK, "Subtask1", "Empty", Status.NEW,
                startTime1.plusMinutes(60), Duration.ofMinutes(9), 3);
        manager.updateSubtask(subtask);
        Assertions.assertEquals(startTime1.plusMinutes(60),
                manager.getAllSubtasksById(4).getStartTime());
    }

    @Test
    public void updateSubTaskWithNull() {
        manager.updateSubtask(null);
        Assertions.assertEquals(subtask1, manager.getAllSubtasksById(4));
    }

    @Test
    public void getPrioritizedTasks() {
        List<Task> list1 = manager.getPrioritizedTasks();
        assertEquals(4, list1.size());
        assertEquals(task1, list1.get(0));
        assertEquals(task2, list1.get(1));
        assertEquals(subtask1, list1.get(2));
        assertEquals(subtask2, list1.get(3));
    }
}