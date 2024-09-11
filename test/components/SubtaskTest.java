package components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void createSubtask() {
        Epic epic = new Epic("Эпик1", "Пусто");
        epic.setId(1);

        Subtask subtask = new Subtask("Подзадача", "Пусто", epic.getId());
        subtask.setId(2);

        assertEquals("Подзадача", subtask.getTaskName());
        assertEquals("Пусто", subtask.getTaskDescription());
        assertEquals(1, subtask.getIdEpic());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    void getIdEpic() {
        Epic epic = new Epic("Эпик1", "Пусто");
        epic.setId(1);

        Subtask subtask = new Subtask("Пусто", "Пусто", epic.getId());
        subtask.setId(2);

        assertEquals(1, subtask.getIdEpic());
    }

}