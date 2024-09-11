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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 26, 1, 0);
        Task task1 = new Task(1, Type.TASK, "Task1", Status.NEW, "Empty", startTime,
                Duration.ofMinutes(9));
        taskManager.addTask(task1);

        Epic epic1 = new Epic(2, Type.EPIC,"Epic1", Status.NEW, "Empty");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask(3, Type.SUBTASK, "Subtask1", "Empty", Status.NEW,
                startTime.plusMinutes(60), Duration.ofMinutes(9), 2);
        taskManager.addSubtask(subtask1);

        Assertions.assertTrue(Files.exists(somePath));

        String lineFromFile = Files.readString(Paths.get(somePath.toUri()));
        String line = "id,type,name,status,description,epic,startTime,duration,endTime" + "\n" +
                "1,TASK,Task1,NEW,Empty,2024-06-26T01:00,PT9M" + "\n" + "2,EPIC,Epic1,NEW,Empty,null,null,null" + "\n" +
                "3,SUBTASK,Subtask1,NEW,Empty,2024-06-26T02:00,PT9M,2" + "\n";
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
        String taskString = "1,TASK,Task1,NEW,Empty,2024-06-26T01:00,PT9M";
        Task task = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals(Type.TASK, task.getType());
        Assertions.assertEquals("Task1", task.getTaskName());
        Assertions.assertEquals(Status.NEW, task.getStatus());
        Assertions.assertEquals("Empty", task.getTaskDescription());

        LocalDateTime expectedStartTime = LocalDateTime.parse("2024-06-26T01:00",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualStartTime = task.getStartTime();
        Assertions.assertEquals(expectedStartTime, actualStartTime);

        Assertions.assertEquals("PT9M", task.getDuration().toString());
    }

    @Test
    void fromStringEpicTest() {
        String taskString = "2,EPIC,Epic1,NEW,Empty,null,null,null";
        Task epic = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(2, epic.getId());
        Assertions.assertEquals(Type.EPIC, epic.getType());
        Assertions.assertEquals("Epic1", epic.getTaskName());
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals("Empty", epic.getTaskDescription());
    }

    @Test
    void fromStringSubtaskTest() {
        String taskString = "3,SUBTASK,Subtask1,NEW,Empty,2024-06-26T02:00,PT9M,2";
        Task subtask = FileBackedTaskManager.fromString(taskString);
        Assertions.assertEquals(3, subtask.getId());
        Assertions.assertEquals(Type.SUBTASK, subtask.getType());
        Assertions.assertEquals("Subtask1", subtask.getTaskName());
        Assertions.assertEquals(Status.NEW, subtask.getStatus());
        Assertions.assertEquals("Empty", subtask.getTaskDescription());

        LocalDateTime expectedStartTime = LocalDateTime.parse("2024-06-26T02:00",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualStartTime = subtask.getStartTime();
        Assertions.assertEquals(expectedStartTime, actualStartTime);

        Assertions.assertEquals("PT9M", subtask.getDuration().toString());
    }
}