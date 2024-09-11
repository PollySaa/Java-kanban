import components.Epic;
import components.Status;
import components.Subtask;
import components.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Пусто");
        Task task2 = new Task("Задача 2", "Пусто");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик1", "Пусто");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Пусто", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Пусто", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик2", "Пусто");
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача1", "Пусто", epic2.getId());
        manager.addSubtask(subtask3);


        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());


        System.out.println("ВЫВОД ПО ID");
        System.out.println(manager.getAllTasksById(2));
        System.out.println(manager.getAllSubtasksById(4));
        System.out.println(manager.getAllEpicsById(3));
        System.out.println(manager.getAllTasksById(2));


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

        System.out.println("ИСТОРИЯ");
        System.out.println(manager.getHistoryTasks());
    }
}
