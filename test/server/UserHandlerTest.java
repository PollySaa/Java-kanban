package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import components.Status;
import components.Task;
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

public class UserHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    Gson gson = taskServer.getGson();
    LocalDateTime startTime1;
    Task task1;
    Task task2;

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        taskServer.start();

        startTime1 = LocalDateTime.of(2024, 6, 28, 1, 0);
        task1 = new Task("Task1","Empty", Status.NEW, startTime1,
                Duration.ofMinutes(9));
        task2 = new Task("Task2","Empty", Status.NEW, startTime1.plusMinutes(60),
                Duration.ofMinutes(9));
        manager.addTask(task1);
        manager.addTask(task2);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        manager.getAllTasksById(task1.getId());
        manager.getAllTasksById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        Assertions.assertNotNull(tasksFromRequest);
        Assertions.assertEquals(2, tasksFromRequest.size());
    }

    @Test
    public void getPrioritize() throws IOException, InterruptedException {
        manager.getAllTasksById(task1.getId());
        manager.getAllTasksById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        Assertions.assertNotNull(tasksFromRequest);
        Assertions.assertEquals(2, tasksFromRequest.size());
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {

    }
}