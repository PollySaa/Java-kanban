package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void testTaskManagerIsInitialized() {
        TaskManager manager = Managers.getDefault();
        assertInstanceOf(TaskManager.class, manager);
    }

    @Test
    void testHistoryManagerIsInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertInstanceOf(HistoryManager.class, historyManager);
    }
}