package server;

import com.google.gson.Gson;
import components.Epic;
import components.Status;
import components.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.Type;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    Gson gson = taskServer.getGson();
    LocalDateTime startTime1;
    Epic epic;
    Subtask subtask;

    public SubtaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllEpics();
        manager.removeAllEpics();
        taskServer.start();

        startTime1 = LocalDateTime.of(2024, 6, 28, 1, 0);

        epic = new Epic(Type.EPIC,"Epic1", Status.NEW, "Empty");
        manager.addEpic(epic);

        subtask = new Subtask("Subtask1", "Empty", Status.NEW,
                startTime1, Duration.ofMinutes(9), epic.getId());
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtask();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Subtask1", tasksFromManager.getFirst().getTaskName());
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        Subtask subtask = new Subtask("Subtask2","Empty", Status.NEW, startTime1,
                Duration.ofMinutes(9), epic.getId());

        String taskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(2, manager.getAllSubtask().getFirst().getId());
        Assertions.assertEquals("Subtask2", manager.getAllSubtasksById(2).getTaskName());
    }

    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Application", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtask();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Subtask1", tasksFromManager.getFirst().getTaskName());
    }

    @Test
    public void getAllSubtasksById() throws IOException, InterruptedException {
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals("Subtask1", manager.getAllSubtasksById(subtask.getId()).getTaskName());
    }

    @Test
    public void removeSubtasksById() throws IOException, InterruptedException {
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtask();

        Assertions.assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void addSubtaskIncorrect() throws IOException, InterruptedException {
        String taskJson = gson.toJson("");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void getAllSubtaskByIdIncorrectId() throws IOException, InterruptedException {
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void removeSubtaskByIdIncorrectId() throws IOException, InterruptedException {
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }
}