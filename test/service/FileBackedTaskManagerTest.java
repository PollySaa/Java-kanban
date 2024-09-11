package service;

import components.Epic;
import components.Status;
import components.Subtask;
import components.Task;
import exceptions.ManagerSaveException;
import org.junit.jupiter.api.*;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static service.FileBackedTaskManager.loadFromFile;

class FileBackedTaskManagerTest {
    private static FileBackedTaskManager taskManager;
    private static final TaskManager manager = Managers.getDefault();
    private static Path somePath;

    @BeforeAll
    static void beforeAll() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        somePath = Paths.get("test.csv");
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), somePath);
    }

    @AfterEach
    void tearDown() {
        taskManager.tasks.clear();
        taskManager.epics.clear();
        taskManager.subtasks.clear();
    }


    @Test
    void saveTasks() throws IOException {
        Task task = new Task(1, Type.TASK, "Task1", Status.NEW, "empty");
        taskManager.addTask(task);

        Epic epic = new Epic(2, Type.EPIC, "Epic1", Status.NEW, "empty");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, Type.SUBTASK, "Subtask1", Status.NEW, "empty",
                2);
        taskManager.addSubtask(subtask);

        Assertions.assertTrue(Files.exists(somePath));

        String lineFromFile = Files.readString(Paths.get(somePath.toUri()));
        String line = "id,type,name,status,description,epic" + "\n" + "1,TASK,Task1,NEW,empty" + "\n" + "2,EPIC," +
                "Epic1,NEW,empty" + "\n" + "3,SUBTASK,Subtask1,NEW,empty,2" + "\n";
        Assertions.assertEquals(line, lineFromFile);
    }

    @Test
    void loadFromFileTest() {
        FileBackedTaskManager fileManager = loadFromFile(new File(String.valueOf(somePath)));

        File nonExistentFile = new File("non_existent_file.txt");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile));

        Assertions.assertEquals(1, fileManager.tasks.size());
        Assertions.assertEquals(1, fileManager.epics.size());
        Assertions.assertEquals(1, fileManager.subtasks.size());
    }

    @Test
    void loadTest() {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), somePath);

        fileManager.load();

        Assertions.assertEquals(1, fileManager.tasks.size());
        Assertions.assertEquals(1, fileManager.epics.size());
        Assertions.assertEquals(1, fileManager.subtasks.size());
    }

    @Test
    void loadInvalidTest() throws IOException {
        File file = File.createTempFile("test", ".txt");
        Path path = Paths.get("test.txt");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), path);
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("invalid_data;1;2;3;4;5;6");
        }
        assertThrows(ManagerSaveException.class, fileManager::load);
    }

    @Test
    void fromStringTaskTest() {
        String taskString = "1,TASK,Task1,NEW,empty";
        Task task = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals(Type.TASK, task.getType());
        Assertions.assertEquals("Task1", task.getTaskName());
        Assertions.assertEquals(Status.NEW, task.getStatus());
        Assertions.assertEquals("empty", task.getTaskDescription());
    }

    @Test
    void fromStringEpicTest() {
        String taskString = "1,EPIC,Epic1,NEW,empty";
        Task epic = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(1, epic.getId());
        Assertions.assertEquals(Type.EPIC, epic.getType());
        Assertions.assertEquals("Epic1", epic.getTaskName());
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals("empty", epic.getTaskDescription());
    }

    @Test
    void fromStringSubtaskTest() {
        String taskString = "2,SUBTASK,Subtask1,NEW,empty,1";
        Task subtask = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(Type.SUBTASK, subtask.getType());
        Assertions.assertEquals("Subtask1", subtask.getTaskName());
        Assertions.assertEquals(Status.NEW, subtask.getStatus());
        Assertions.assertEquals("empty", subtask.getTaskDescription());
    }
}