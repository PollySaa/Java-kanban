package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private int port = 8080;
    private static HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(int port, TaskManager taskManager) {
        this.port = port;
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(port), 0);
            UserHandler userHandler = new UserHandler(gson, taskManager);
            httpServer.createContext("/tasks", new TaskHandler(gson, taskManager, "tasks"));
            httpServer.createContext("/subtasks", new SubtaskHandler(gson, taskManager, "subtasks"));
            httpServer.createContext("/epics", new EpicHandler(gson, taskManager, "epics"));
            httpServer.createContext("/history", userHandler);
            httpServer.createContext("/prioritized", userHandler);
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void stop() {
        httpServer.stop(0);
    }

    public Gson getGson() {
        return this.gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(8080, taskManager);
        httpTaskServer.start();
        System.out.println("Нажмите ENTER для остановки сервера...");
        System.in.read();
        httpTaskServer.stop();
    }

}
