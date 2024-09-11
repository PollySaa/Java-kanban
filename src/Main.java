import components.Epic;
import components.Status;
import components.Subtask;
import components.Task;
import service.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        LocalDateTime startTime = LocalDateTime.of(2024, 6, 26, 1, 0);

        Task task1 = new Task(Type.TASK, "Task1", Status.NEW, "Empty",
                startTime, Duration.ofMinutes(9));
        Task task2 = new Task(Type.TASK, "Task2", Status.NEW, "Empty",
                startTime.plusMinutes(30), Duration.ofMinutes(9));
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic(Type.EPIC,"Epic1", Status.NEW, "Empty");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask(Type.SUBTASK, "Subtask1", Status.NEW, "Empty",
                startTime.plusMinutes(60), Duration.ofMinutes(9), epic1.getId());
        Subtask subtask2 = new Subtask(Type.SUBTASK, "Subtask2", Status.NEW, "Empty",
                startTime.plusMinutes(40), Duration.ofMinutes(9), epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic(Type.EPIC,"Epic3", Status.NEW, "Empty");
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask(Type.SUBTASK, "Subtask1", Status.NEW, "Empty",
                startTime.plusMinutes(20), Duration.ofMinutes(9), epic2.getId());
        manager.addSubtask(subtask3);

        System.out.println();
        System.out.println("ПОЛНЫЙ ВЫВОД");
        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        System.out.println();
        System.out.println("ВЫВОД ПО ID");
        System.out.println(manager.getAllTasksById(1));
        System.out.println(manager.getAllSubtasksById(4));
        System.out.println(manager.getAllEpicsById(3));
        System.out.println(manager.getAllTasksById(2));

        System.out.println();
        System.out.println("ИСТОРИЯ");
        System.out.println(manager.getHistoryTasks());

        System.out.println();
        System.out.println("СМЕНА СТАТУСОВ");
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        task2.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task2);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(Status.NEW);
        manager.updateSubtask(subtask3);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());


        System.out.println();
        System.out.println("УДАЛЕНИЕ");
        manager.removeTaskById(2);
        System.out.println(manager.getAllTask());
        manager.removeEpicById(3);
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        System.out.println();
        System.out.println("ИСТОРИЯ");
        System.out.println(manager.getHistoryTasks());

        System.out.println();
        System.out.println("СОРТИРОВКА");
        System.out.println(manager.getPrioritizedTasks());
        manager.removeAllTasks();
        System.out.println(manager.getPrioritizedTasks());
    }
}
