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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    Gson gson = taskServer.getGson();
    LocalDateTime startTime1;
    Epic epic;
    Subtask subtask;

    public EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllEpics();
        manager.removeAllEpics();
        taskServer.start();

        epic = new Epic("Epic1", "Empty", Status.NEW);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpic();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Epic1", tasksFromManager.getFirst().getTaskName());
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        Epic epic = new Epic("Epic2","Empty", Status.NEW);

        String taskJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, manager.getAllEpic().getFirst().getId());
        Assertions.assertEquals("Epic2", manager.getAllEpicsById(1).getTaskName());
    }

    @Test
    public void getAllEpics() throws IOException, InterruptedException {
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Application", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpic();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Epic1", tasksFromManager.getFirst().getTaskName());
    }

    @Test
    public void getAllEpicsById() throws IOException, InterruptedException {
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals("Epic1", manager.getAllEpicsById(epic.getId()).getTaskName());
    }

    @Test
    public void removeEpicsById() throws IOException, InterruptedException {
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpic();

        Assertions.assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void addEpicIncorrect() throws IOException, InterruptedException {
        String taskJson = gson.toJson("");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void getAllEpicByIdIncorrectId() throws IOException, InterruptedException {
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void removeEpicByIdIncorrectId() throws IOException, InterruptedException {
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void getSubtaskOfEpic() throws IOException, InterruptedException {
        manager.addEpic(epic);
        startTime1 = LocalDateTime.of(2024, 6, 28, 1, 0);
        subtask = new Subtask("Subtask1", "Empty", Status.NEW,
                startTime1, Duration.ofMinutes(9), epic.getId());
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }
}