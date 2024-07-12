package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import components.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpTask {

    public TaskHandler(Gson gson, TaskManager taskManager, String path) {
        super(gson, taskManager, path);
    }

    @Override
    void handleAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTask());
        sendText(exchange, response);
    }

    @Override
    void handleById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> id = getIdFromPath(exchange);

            if (id.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Task task = taskManager.getAllTasksById(id.get());

            if (task == null) {
                sendNotFound(exchange);
                return;
            }

            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleAddOrUpdate(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            Optional<Task> optionalTask = parseTask(input);
            if (optionalTask.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Task task = optionalTask.get();

            try {
                if (Optional.ofNullable(task.getId()).isPresent()) {
                    taskManager.updateTask(task);
                    sendText(exchange, "Задача успешно обновлена");
                } else {
                    taskManager.addTask(task);
                    sendText(exchange, "Задача успешно добавлена");
                }
            } catch (Exception e) {
                sendHasInteractions(exchange);
            }
        }
    }

    @Override
    void handleRemove(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> id = getIdFromPath(exchange);

            if (id.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            taskManager.removeTaskById(id.get());
            sendText(exchange, "Задача успешно удалена");
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleSubtaskOfEpic(HttpExchange exchange) throws IOException {

    }

    private Optional<Task> parseTask(InputStream bodyInput) throws IOException {
        String body = new String(bodyInput.readAllBytes(), StandardCharsets.UTF_8);
        if (!body.contains("taskName") || !body.contains("taskDescription") || !body.contains("status") || !body.contains("startTime")) {
            return Optional.empty();
        }

        Task task = gson.fromJson(body, Task.class);

        return Optional.of(task);
    }

}
