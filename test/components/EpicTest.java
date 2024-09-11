package components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        epic1 = new Epic("Эпик1", "Пусто");
        epic1.setId(1);

        subtask1 = new Subtask("Подзадача1", "Пусто", epic1.getId());
        subtask1.setId(2);

        subtask2 = new Subtask("Подзадача2", "Пусто", epic1.getId());
        subtask1.setId(3);
    }

    @Test
    void addSubtasks() {
        epic1.addSubtasks(subtask1);
        epic1.addSubtasks(subtask2);
        assertEquals(2, epic1.getSubtasks().size());
    }

    @Test
    void getSubtasks() {
        epic1.addSubtasks(subtask1);

        assertEquals(1, epic1.getSubtasks().size());
        assertEquals(subtask1, epic1.getSubtasks().getFirst());
    }

    @Test
    void updateSubtasks() {
        epic1.addSubtasks(subtask1);
        subtask1.setStatus(Status.DONE);
        epic1.updateSubtasks(subtask1);

        assertEquals(Status.DONE, epic1.getSubtasks().getFirst().getStatus());
    }

    @Test
    void removeSubtasks() {
        epic1.addSubtasks(subtask1);
        epic1.addSubtasks(subtask2);
        epic1.removeSubtasks(subtask1.getId());
        epic1.removeSubtasks(subtask2.getId());
        assertEquals(0, epic1.getSubtasks().size());
    }

    @Test
    void removeAllSubtasks() {
        epic1.addSubtasks(subtask1);
        epic1.addSubtasks(subtask2);
        epic1.removeAllSubtasks();
        assertEquals(0, epic1.getSubtasks().size());
    }

    @Test
    void updateStatusToNew() {
        Epic epic1 = new Epic("Эпик1", "Пусто");
        epic1.setId(1);

        epic1.updateStatus();
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void updateStatusToDone() {
        epic1.addSubtasks(subtask1);
        epic1.addSubtasks(subtask2);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic1.updateStatus();
        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void updateStatusToInProgress() {
        epic1.addSubtasks(subtask1);
        epic1.addSubtasks(subtask2);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        epic1.updateStatus();
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

}