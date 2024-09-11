package service;

import components.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TaskManager manager = Managers.getDefault();
    private Task task1;
    private Task task2;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Задача 1", "Пусто");
        manager.addTask(task1);

        task2 = new Task("Задача 2", "Пусто");
        manager.addTask(task2);
    }

    @Test
    void testAddWhenTaskNotExists() {
        assertFalse(historyManager.getHistory().contains(task1));
        historyManager.add(task1);
        assertTrue(historyManager.getHistory().contains(task1));
    }

    @Test
    void testAddWhenTaskExists() {
        historyManager.add(task1);
        assertTrue(historyManager.getHistory().contains(task1));
        historyManager.add(task1);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        assertEquals(tasks, historyManager.getHistory());
    }

    @Test
    void testRemoveWhenTaskExists() {
        historyManager.add(task1);
        assertTrue(historyManager.getHistory().contains(task1));
        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1));
    }

    @Test
    void testRemoveWhenTaskNotExists() {
        assertFalse(historyManager.getHistory().contains(task1));
        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1));
    }

    @Test
    void testGetHistoryWhenEmpty() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testGetHistoryWhenNotEmpty() {
        historyManager.add(task1);
        assertFalse(historyManager.getHistory().isEmpty());
    }

    @Test
    void testLinkLastWhenEmpty() {
        historyManager.linkLast(task1);
        assertEquals(task1, historyManager.getHistory().getFirst());
    }

    @Test
    void testLinkLastWhenNotEmpty() {
        historyManager.linkLast(task1);
        historyManager.linkLast(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        assertEquals(tasks, historyManager.getHistory());
    }


}