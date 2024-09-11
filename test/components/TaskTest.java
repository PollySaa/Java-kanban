package components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

class TaskTest {
    TaskManager manager = Managers.getDefault();

    @Test
    public void testEqualTasks() {
        Task task1 = new Task("Задача1", "Пусто");
        Task task2 = new Task("Задача2", "Пусто");
        manager.addTask(task1);
        manager.addTask(task2);
        Assertions.assertNotEquals(task1.getId(), task2.getId());
    }


}